package com.example;

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/countries")
public class CountryController {

    @GetMapping
    public ResponseEntity<List<String>> getCountries() throws InterruptedException {
        Thread.sleep(5000); // Simulate delay
        List<String> countries = List.of("USA", "Germany", "Japan", "Brazil", "Australia");

        return ResponseEntity.ok()
                .cacheControl(
                        CacheControl.maxAge(1, TimeUnit.HOURS)
                )
                .body(countries);
    }
}

