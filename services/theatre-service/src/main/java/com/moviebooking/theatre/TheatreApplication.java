package com.moviebooking.theatre;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Theatre Service Application
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableCaching
@EnableKafka
public class TheatreApplication {

    public static void main(String[] args) {
        SpringApplication.run(TheatreApplication.class, args);
    }
}