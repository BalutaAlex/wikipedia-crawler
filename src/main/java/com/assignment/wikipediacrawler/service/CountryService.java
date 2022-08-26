package com.assignment.wikipediacrawler.service;

import com.assignment.wikipediacrawler.entities.City;
import com.assignment.wikipediacrawler.entities.Country;
import com.assignment.wikipediacrawler.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Service
public class CountryService {

    @Autowired
    CountryRepository countryRepository;

    public boolean saveCountry(Country country) {
        try {
            countryRepository.save(country);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public Country findCountryByName(String countryName){
        try {
            return countryRepository.findByName(countryName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public List<Country> findAll(){
        return countryRepository.findAll();
    }


    public boolean saveAll(Iterable<Country> countries){
        try {
            countryRepository.saveAll(countries);
        } catch (Exception e){
            return false;
        }
        return true;
    }

}
