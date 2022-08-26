package com.assignment.wikipediacrawler.crawler;

import com.assignment.wikipediacrawler.entities.City;
import com.assignment.wikipediacrawler.entities.Country;
import com.assignment.wikipediacrawler.service.CityService;
import com.assignment.wikipediacrawler.service.CountryService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class CrawlerAlgorithm {

    private static final String URL1 = "https://en.wikipedia.org/wiki/List_of_countries_by_population_in_2010";
    private static final String URL2 = "https://en.wikipedia.org/wiki/List_of_largest_cities";
    private static final String URL3 = "https://en.wikipedia.org/wiki/List_of_towns_and_cities_with_100,000_or_more_inhabitants/country:";

    @Autowired
    CountryService countryService;
    @Autowired
    CityService cityService;

    public void getCountries() throws IOException {
        List<Country> countries = new ArrayList<>();

        Document doc = Jsoup.connect(URL1).get();
        Elements tableRows = doc.select(".wikitable.sortable tr");
        int i = 1;
        for (Element row : tableRows) {
            if (i <= 2) {
                i++;
                continue;
            }
            String name = row.children().get(1).text();

            String populationText = row.children().get(2).text().replaceAll(",", "");
            int population = 0;
            if (!populationText.isEmpty()) {
                population = Integer.parseInt(populationText);
            }

            String areaText = row.children().get(4).text().replaceAll(",", "");
            double area = 0.0;
            if (!areaText.isEmpty()) {
                area = Double.parseDouble(areaText);
            }

            Country country = new Country(name, population, area);
            countries.add(country);
        }
        countryService.saveAll(countries);
    }

    public void getCities() throws IOException {
        List<City> cities = new ArrayList<>();
        Map<String, Country> countryMap = convertListOfCountriesToHashMap(countryService.findAll());

        Document doc = Jsoup.connect(URL2).get();
        Elements tableRows = doc.select(".wikitable.sortable tr");

        for (int i = 3; i < tableRows.size(); i++) {
            Element row = tableRows.get(i);
            String cityName = row.children().get(0).text();
            String populationText = row.children().get(4).text();
            int population = 0;
            if (!populationText.equals("—")) population = Integer.parseInt(populationText.replaceAll(",", ""));
            String densityText = row.children().get(6).text();
            double density = 0.0;
            if (!densityText.equals("—")) {
                String[] arr = densityText.split(" ");
                densityText = arr[0].replaceAll(",", "");
                density = Double.parseDouble(densityText);
            }

            String a = row.children().get(1).text();
            Country country = getCountryByName(countryMap, row.children().get(1).text());

            if (country == null) continue;
            cities.add(new City(country, cityName, population, density));
        }
        cityService.saveAll(cities);
    }

    public void getPopulatedCities() {
        String[] params = {"_A-B", "_C-D-E-F", "_G-H-I-J-K", "_L-M-N-O", "_P-Q-R-S", "_T-U-V-W-Y-Z"};

        ExecutorService executorService = Executors.newFixedThreadPool(6);

        for (int i = 0; i < params.length; i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    getDataFromWikipediaPage(params[index]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        executorService.shutdown();

    }

    public void getDataFromWikipediaPage(String urlParameters) throws IOException {
        Document doc = Jsoup.connect(URL3 + urlParameters).get();
        Elements tablesAndLists = doc.select(".wikitable.sortable,h2+ul");
        Elements countriesHeadlines = doc.select("h2 .mw-headline, h3 .mw-headline");

        List<City> result = new ArrayList<>(2000);
        Map<String, Country> countryMap = convertListOfCountriesToHashMap(countryService.findAll());

        int countryIndex = -1;
        int step = 1;
        int index = 0;

        for (int i = 0; i < tablesAndLists.size() - 2; i++) {

            if (countriesHeadlines.get(index++).parent().is("h3")) {
                if (step == 1) {
                    index++;
                    step++;
                }
                step++;
            } else {
                countryIndex += step;
                step = 1;
            }

            Element element = tablesAndLists.get(i);
            if (element.is("table")) {
                Elements tableRows = element.select("tr");
                for (Element tr : tableRows) {
                    String name = tr.children().get(0).text();
                    if (name.equals("Name") || name.equals("City")) {
                        continue;
                    }
                    String populationText = tr.children().get(tr.children().size() == 2 ? 1 : 2).text().replaceAll(",", "");
                    int population = 0;
                    if (!populationText.isEmpty()) {
                        population = Integer.parseInt(populationText.split(" ")[0]);
                    }

                    String countryName = countriesHeadlines.get(countryIndex).text();

                    Country country = getCountryByName(countryMap, countryName);

                    if (country == null) {
                        continue;
                    }

                    result.add(new City(country, name, population, 0.0));

                }
            } else if (element.is("ul")) {
                List<String> liText = element.children().eachText();
                for (String li : liText) {

                    String name = li.split("[0-9]")[0];

                    int population;
                    String populationText = li.replaceAll("[^\\d]", "");
                    populationText = populationText.trim();
                    if (!populationText.isEmpty()) {
                        population = Integer.parseInt(populationText);
                    } else {
                        population = 0;
                    }

                    String countryName = countriesHeadlines.get(countryIndex).text();
                    Country country = getCountryByName(countryMap, countryName);

                    if (country == null) {
                        continue;
                    }

                    result.add(new City(country, name, population, 0.0));
                }
            }
        }
        cityService.saveAll(result);
    }


    private HashMap<String, Country> convertListOfCountriesToHashMap(List<Country> list) {
        HashMap<String, Country> countryMap = new HashMap<>();
        for (Country c : list) {
            countryMap.put(c.getName(), c);
        }
        return countryMap;
    }

    private Country getCountryByName(Map<String, Country> countryMap, String countryName) {

        Country country = countryMap.get(countryName);
        if (country == null) {
            for (String countryKey : countryMap.keySet()) {
                if (
                        countryName.contains(countryKey) ||
                                countryKey.contains(countryName.split(",")[0]) ||
                                Normalizer.normalize(countryName, Normalizer.Form.NFKD).replaceAll("\\p{M}", "").contains(countryKey)
                ) {
                    country = countryMap.get(countryKey);
                    break;
                }
            }
        }
        return country;
    }
}

