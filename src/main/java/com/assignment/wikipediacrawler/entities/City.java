package com.assignment.wikipediacrawler.entities;


import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "cities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    private String name;
    private Integer population;
    private Double density;

    public City(Country country, String name, Integer population, Double density) {
        this.country = country;
        this.name = name;
        this.population = population;
        this.density = density;
    }

    @Override
    public String toString() {
        return "City{" +
                "name='" + name + '\'' +
                ", country=" + country.getName()+
                ", population=" + population +
                ", density=" + density +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        City city = (City) o;
        return Objects.equals(id, city.id) && Objects.equals(name, city.name) && Objects.equals(population, city.population) && Objects.equals(density, city.density);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, population, density);
    }

}
