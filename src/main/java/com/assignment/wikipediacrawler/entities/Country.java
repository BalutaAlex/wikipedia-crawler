package com.assignment.wikipediacrawler.entities;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "countries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "country_name")
    private String name;

    private Integer population;
    private Double area;

    public Country(String name, Integer population, Double area) {
        this.name = name;
        this.population = population;
        this.area = area;
    }

    @OneToMany(mappedBy = "country",fetch = FetchType.EAGER)
    private Set<City> cities;

    @Override
    public String toString() {
        return "Country{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", population=" + population +
                ", area=" + area +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Country country = (Country) o;
        return Objects.equals(id, country.id) && Objects.equals(name, country.name) && Objects.equals(population, country.population) && Objects.equals(area, country.area);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, population, area);
    }
}
