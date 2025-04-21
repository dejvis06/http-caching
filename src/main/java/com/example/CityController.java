package com.example;

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/cities")
public class CityController {

    private final CityCsvService cityCsvService;

    public CityController(CityCsvService cityCsvService) {
        this.cityCsvService = cityCsvService;
    }

    @GetMapping
    public ResponseEntity<List<CityCsvService.City>> getCities() throws InterruptedException {
        Thread.sleep(3000); // Simulate Delay
        return ResponseEntity.ok()
                .cacheControl(
                        CacheControl.maxAge(10, TimeUnit.SECONDS)
                )
                .body(cityCsvService.loadCities());
    }
}

