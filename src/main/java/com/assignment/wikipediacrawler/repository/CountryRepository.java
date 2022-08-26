package com.assignment.wikipediacrawler.repository;

import com.assignment.wikipediacrawler.entities.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CountryRepository extends JpaRepository<Country, Long> {

    @Query("SELECT c FROM Country c WHERE c.name = ?1")
    Country findByName(String countryName);

}
