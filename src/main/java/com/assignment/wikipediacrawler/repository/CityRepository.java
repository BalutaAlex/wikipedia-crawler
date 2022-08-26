package com.assignment.wikipediacrawler.repository;

import com.assignment.wikipediacrawler.entities.City;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CityRepository extends JpaRepository<City, Long> {
}
