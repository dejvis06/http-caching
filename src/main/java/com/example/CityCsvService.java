package com.example;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.util.List;

@Service
@Slf4j
public class CityCsvService {

    public List<City> loadCities() {
        log.info("Loading city data from CSV...");
        try (InputStreamReader reader = new InputStreamReader(
                getClass().getResourceAsStream("/worldcities.csv"))) {

            CsvToBean<CityCsvRaw> csvToBean = new CsvToBeanBuilder<CityCsvRaw>(reader)
                    .withType(CityCsvRaw.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<City> cityList = csvToBean.stream()
                    .map(CityCsvRaw::toCity)
                    .peek(city -> log.debug("Parsed city: {}", city))
                    .toList();

            log.info("Successfully loaded {} cities.", cityList.size());
            return cityList;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load cities", e);
        }
    }

    @Data
    public static class City {
        private String city;
        private String cityAscii;
        private double lat;
        private double lng;
        private String country;
        private String iso2;
        private String iso3;
        private String adminName;
        private String capital;
        private String population;
    }

    @Data
    public static class CityCsvRaw {

        @CsvBindByName
        private String city;

        @CsvBindByName(column = "city_ascii")
        private String cityAscii;

        @CsvBindByName
        private double lat;

        @CsvBindByName
        private double lng;

        @CsvBindByName
        private String country;

        @CsvBindByName
        private String iso2;

        @CsvBindByName
        private String iso3;

        @CsvBindByName(column = "admin_name")
        private String adminName;

        @CsvBindByName
        private String capital;

        @CsvBindByName
        private String population;

        public City toCity() {
            City c = new City();
            c.setCity(this.city);
            c.setCityAscii(this.cityAscii);
            c.setLat(this.lat);
            c.setLng(this.lng);
            c.setCountry(this.country);
            c.setIso2(this.iso2);
            c.setIso3(this.iso3);
            c.setAdminName(this.adminName);
            c.setCapital(this.capital);
            c.setPopulation(this.population);
            return c;
        }
    }
}

