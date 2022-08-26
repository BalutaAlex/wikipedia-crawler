package com.assignment.wikipediacrawler;

import com.assignment.wikipediacrawler.crawler.CrawlerAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WikipediaCrawlerApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(WikipediaCrawlerApplication.class, args);
    }

    @Autowired
    CrawlerAlgorithm crawlerAlgorithm;

    @Override
    public void run(String... args) throws Exception {

        //task 1
        crawlerAlgorithm.getCountries();
        crawlerAlgorithm.getCities();
        //task optional
        crawlerAlgorithm.getPopulatedCities();

    }
}
