package com.moviebooking.shared.util;

/**
 * Application-wide constants
 */
public class Constants {
    
    // Kafka Topics
    public static final String BOOKING_CREATED_TOPIC = "booking-created";
    public static final String BOOKING_CONFIRMED_TOPIC = "booking-confirmed";
    public static final String BOOKING_CANCELLED_TOPIC = "booking-cancelled";
    public static final String PAYMENT_INITIATED_TOPIC = "payment-initiated";
    public static final String PAYMENT_COMPLETED_TOPIC = "payment-completed";
    public static final String PAYMENT_FAILED_TOPIC = "payment-failed";
    public static final String INVENTORY_UPDATED_TOPIC = "inventory-updated";
    public static final String SHOW_CREATED_TOPIC = "show-created";
    public static final String SHOW_UPDATED_TOPIC = "show-updated";
    
    // Offer Types
    public static final String OFFER_THIRD_TICKET_DISCOUNT = "THIRD_TICKET_DISCOUNT";
    public static final String OFFER_AFTERNOON_DISCOUNT = "AFTERNOON_DISCOUNT";
    
    // Booking Status
    public static final String BOOKING_PENDING = "PENDING";
    public static final String BOOKING_CONFIRMED = "CONFIRMED";
    public static final String BOOKING_CANCELLED = "CANCELLED";
    public static final String BOOKING_EXPIRED = "EXPIRED";
    
    // Payment Status
    public static final String PAYMENT_PENDING = "PENDING";
    public static final String PAYMENT_SUCCESS = "SUCCESS";
    public static final String PAYMENT_FAILED = "FAILED";
    
    // Show Timings
    public static final String MORNING_SHOW = "MORNING";
    public static final String AFTERNOON_SHOW = "AFTERNOON";
    public static final String EVENING_SHOW = "EVENING";
    public static final String NIGHT_SHOW = "NIGHT";
    
    // Cache Keys
    public static final String CACHE_MOVIES = "movies";
    public static final String CACHE_THEATRES = "theatres";
    public static final String CACHE_SHOWS = "shows";
    public static final String CACHE_SEATS = "seats";
    public static final String CACHE_OFFERS = "offers";
    
    // Pagination
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;
}
