package com.moviebooking.movie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Movie Catalogue Service Application
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableCaching
public class MovieCatalogueApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieCatalogueApplication.class, args);
    }
}