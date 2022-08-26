package com.assignment.wikipediacrawler.service;

import com.assignment.wikipediacrawler.entities.City;
import com.assignment.wikipediacrawler.repository.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CityService {

    @Autowired
    CityRepository cityRepository;

    public boolean saveCity(City city) {
        try {
            cityRepository.save(city);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean saveAll(Iterable<City> cities){
        try {
            cityRepository.saveAll(cities);
        } catch (Exception e){
            return false;
        }
        return true;
    }

}
