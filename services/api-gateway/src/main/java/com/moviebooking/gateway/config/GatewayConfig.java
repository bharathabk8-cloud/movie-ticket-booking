package com.moviebooking.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * API Gateway route configuration
 */
@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                // Movie Catalogue Service
                .route("movie-catalogue", r -> r
                        .path("/api/v1/movies/**")
                        .uri("lb://movie-catalogue-service"))
                
                // Theatre Service
                .route("theatre", r -> r
                        .path("/api/v1/theatres/**")
                        .uri("lb://theatre-service"))
                
                // Show Service
                .route("show", r -> r
                        .path("/api/v1/shows/**")
                        .uri("lb://show-service"))
                
                // Booking Service
                .route("booking", r -> r
                        .path("/api/v1/bookings/**")
                        .uri("lb://booking-service"))
                
                // Payment Service
                .route("payment", r -> r
                        .path("/api/v1/payments/**")
                        .uri("lb://payment-service"))
                
                // Offer Service
                .route("offer", r -> r
                        .path("/api/v1/offers/**")
                        .uri("lb://offer-service"))
                
                // Inventory Service
                .route("inventory", r -> r
                        .path("/api/v1/inventory/**")
                        .uri("lb://inventory-service"))
                
                // Analytics Service
                .route("analytics", r -> r
                        .path("/api/v1/analytics/**")
                        .uri("lb://analytics-service"))
                
                .build();
    }
}