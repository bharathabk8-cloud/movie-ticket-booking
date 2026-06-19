# API Documentation

## Base URL: http://localhost:8080/api/v1

## Movies
- `GET /movies` - All movies
- `GET /movies/{id}` - Movie by ID
- `GET /movies/genre/{genre}` - By genre
- `POST /movies` - Create movie

## Theatres
- `GET /theatres` - All theatres
- `GET /theatres/city/{city}` - By city
- `POST /theatres` - Create theatre

## Shows
- `GET /shows/movie/{movieId}` - By movie
- `GET /shows/theatre/{theatreId}/date/{date}` - By theatre & date
- `POST /shows` - Create show

## Bookings
- `POST /bookings` - Create booking
- `GET /bookings/{id}` - Get booking
- `GET /bookings/user/{userId}` - User bookings
- `POST /bookings/{id}/confirm` - Confirm
- `POST /bookings/{id}/cancel` - Cancel

## Payments
- `POST /payments/initiate` - Initiate
- `POST /payments/process/{transactionId}` - Process
- `GET /payments/{transactionId}` - Get payment
- `POST /payments/refund/{transactionId}` - Refund

## Offers
- `GET /offers/{offerCode}` - Get offer
- `GET /offers/city/{city}` - City offers
- `GET /offers/calculate-discount` - Calculate discount

## Inventory
- `GET /inventory/show/{showId}` - Get inventory
- `POST /inventory/show/{showId}/book` - Book seats
- `POST /inventory/show/{showId}/release` - Release seats

## Analytics
- `GET /analytics/city/{city}` - City analytics
- `GET /analytics/theatre/{theatreId}` - Theatre analytics
- `GET /analytics/date/{date}` - Daily analytics