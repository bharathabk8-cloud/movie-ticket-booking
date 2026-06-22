# 🎬 Movie Ticket Booking Platform

> A scalable, cloud-ready Movie Ticket Booking Platform built using **Java, Spring Boot, MySQL, Kafka, Docker, and REST APIs**. The platform enables customers to browse movies, view theatre shows, reserve seats, apply promotional offers, and securely book tickets while maintaining seat inventory consistency across multiple theatre partners.

---

# Table of Contents

* Project Overview
* Problem Statement
* Objectives
* Features Implemented
* Technology Stack
* Solution Architecture
* Project Structure
* Design Principles
* Getting Started
* Prerequisites
* Installation
* Running the Application
* Docker Deployment

---

# Project Overview

XYZ is a theatre aggregation platform that integrates with multiple theatres and enables customers to book movie tickets through online channels such as Web and Mobile applications.

The platform provides:

* Browse movies by city
* View theatres and show timings
* Reserve preferred seats
* Online payment processing
* Booking cancellation
* Real-time seat inventory updates
* Discount management
* Event-driven communication using Kafka

The solution has been designed following enterprise architecture principles with scalability, availability, maintainability, and future extensibility in mind.

---

# Problem Statement

The objective is to design and implement a highly scalable movie ticket booking platform capable of:

* Managing thousands of theatres
* Synchronizing seat inventory
* Handling concurrent booking requests
* Supporting secure payment processing
* Integrating with external theatre systems
* Providing RESTful APIs
* Maintaining high availability
* Supporting future omnichannel integrations

---

# Objectives

The implementation focuses on:

* Clean Architecture
* SOLID Principles
* RESTful API Design
* Layered Architecture
* Exception Handling
* Unit Testing
* Docker Containerization
* Event-Driven Communication
* Scalable Database Design
* Enterprise Coding Standards

---

# Features Implemented

## Customer Features

* Browse movie catalogues by city
* Search movies by date
* View theatres running selected movies
* View show timings
* Check seat availability
* Book tickets
* Cancel bookings
* View booking details
* Secure payment processing

---

## Theatre Management

* Create shows
* Update shows
* Delete shows
* Allocate seat inventory
* Update seat availability
* Manage theatres
* Manage movie schedules

---

## Discount Engine

Implemented promotional offers:

* 50% discount on every third ticket
* 20% discount for afternoon shows

Discount calculation is handled automatically during booking.

---

## Booking Features

* Seat reservation
* Payment processing
* Booking confirmation
* Booking cancellation
* Refund processing
* Booking history
* Inventory updates

---

## Kafka Integration

The application publishes booking events to Kafka.

Example events include:

* Booking Created
* Booking Cancelled
* Payment Completed
* Refund Initiated
* Seat Inventory Updated

This enables loose coupling between services and supports future integrations.

---

# Technology Stack

| Layer            | Technology                  |
| ---------------- | --------------------------- |
| Language         | Java 17                     |
| Framework        | Spring Boot                 |
| Build Tool       | Maven                       |
| Database         | MySQL                       |
| ORM              | Spring Data JPA / Hibernate |
| Messaging        | Apache Kafka                |
| API              | REST                        |
| Validation       | Jakarta Validation          |
| Testing          | JUnit 5, Mockito            |
| Documentation    | Swagger/OpenAPI             |
| Containerization | Docker                      |
| Logging          | SLF4J + Logback             |
| Version Control  | Git & GitHub                |

---

# High Level Architecture

The system follows a layered architecture.

```text
                Client Applications
             (Web / Mobile / Admin)
                       │
                       ▼
                REST Controllers
                       │
                       ▼
              Service Layer (Business Logic)
                       │
      ┌────────────────┼────────────────┐
      ▼                ▼                ▼
 Booking Service   Payment Service   Offer Service
      │                │                │
      └────────────────┼────────────────┘
                       ▼
               Repository Layer
                       │
                Spring Data JPA
                       │
                     MySQL
                       │
                 Kafka Producer
                       │
                 External Systems
```

The architecture promotes separation of concerns and simplifies future service decomposition.

---

# Project Structure

```text
movie-ticket-booking
│
├── src
│   ├── main
│   │   ├── controller
│   │   ├── service
│   │   ├── repository
│   │   ├── entity
│   │   ├── dto
│   │   ├── mapper
│   │   ├── config
│   │   ├── kafka
│   │   ├── exception
│   │   └── util
│   │
│   └── test
│       ├── controller
│       ├── service
│       └── repository
│
├── diagrams
│
├── docker
│
├── postman
│
├── README.md
│
└── pom.xml
```

---

# Design Principles

The project follows enterprise software engineering practices.

## SOLID Principles

* Single Responsibility Principle
* Open/Closed Principle
* Liskov Substitution Principle
* Interface Segregation Principle
* Dependency Inversion Principle

---

## Object-Oriented Programming

* Encapsulation
* Abstraction
* Inheritance
* Polymorphism

---

## Layered Architecture

* Controller Layer
* Service Layer
* Repository Layer
* Database Layer

---

## Exception Handling

Global exception handling is implemented using:

* `@ControllerAdvice`
* Custom Exceptions
* Standardized Error Responses

---

## Logging

Application logging is implemented using:

* SLF4J
* Logback

Logs include:

* API Requests
* Booking Events
* Payment Events
* Exceptions
* Kafka Events

---

# Getting Started

## Prerequisites

Install the following software:

* Java 17+
* Maven 3.9+
* MySQL 8+
* Apache Kafka
* Docker Desktop
* Git

---

# Installation

Clone the repository:

```bash
git clone https://github.com/<your-github-username>/movie-ticket-booking.git
```

Navigate to the project:

```bash
cd movie-ticket-booking
```

Build the project:

```bash
mvn clean install
```

---

# Database Configuration

Update the following properties in `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/movie_booking

spring.datasource.username=root

spring.datasource.password=password

spring.jpa.hibernate.ddl-auto=update
```

---

# Running the Application

Using Maven:

```bash
mvn spring-boot:run
```

Or execute the generated JAR:

```bash
java -jar target/movie-ticket-booking.jar
```

Application starts on:

```text
http://localhost:8080
```

---

# Docker Deployment

Build Docker image:

```bash
docker build -t movie-booking .
```

Run Docker container:

```bash
docker run -p 8080:8080 movie-booking
```

Using Docker Compose:

```bash
docker-compose up -d
```

---

# Verification

After starting the application, verify:

* Application starts successfully
* Database connection established
* Kafka producer initializes
* REST endpoints are accessible
* Swagger UI loads successfully
* Unit tests pass
* Docker container runs without errors

---

# REST API Documentation

The application exposes RESTful APIs following standard HTTP methods and status codes.

## Movie APIs

| Method | Endpoint              | Description     |
| ------ | --------------------- | --------------- |
| GET    | `/api/v1/movies`      | Get all movies  |
| GET    | `/api/v1/movies/{id}` | Get movie by ID |
| POST   | `/api/v1/movies`      | Create movie    |
| PUT    | `/api/v1/movies/{id}` | Update movie    |
| DELETE | `/api/v1/movies/{id}` | Delete movie    |

---

## Theatre APIs

| Method | Endpoint                | Description      |
| ------ | ----------------------- | ---------------- |
| GET    | `/api/v1/theatres`      | Get all theatres |
| GET    | `/api/v1/theatres/{id}` | Get theatre      |
| POST   | `/api/v1/theatres`      | Create theatre   |
| PUT    | `/api/v1/theatres/{id}` | Update theatre   |
| DELETE | `/api/v1/theatres/{id}` | Delete theatre   |

---

## Show APIs

| Method | Endpoint                            | Description      |
| ------ | ----------------------------------- | ---------------- |
| GET    | `/api/v1/shows`                     | Get all shows    |
| GET    | `/api/v1/shows/movie/{movieId}`     | Shows by movie   |
| GET    | `/api/v1/shows/theatre/{theatreId}` | Shows by theatre |
| POST   | `/api/v1/shows`                     | Create show      |
| PUT    | `/api/v1/shows/{id}`                | Update show      |
| DELETE | `/api/v1/shows/{id}`                | Delete show      |

---

## Booking APIs

| Method | Endpoint                         | Description     |
| ------ | -------------------------------- | --------------- |
| POST   | `/api/v1/bookings`               | Book tickets    |
| GET    | `/api/v1/bookings/{id}`          | Booking details |
| GET    | `/api/v1/bookings/user/{userId}` | User bookings   |
| PUT    | `/api/v1/bookings/{id}/cancel`   | Cancel booking  |

---

## Payment APIs

| Method | Endpoint                  | Description    |
| ------ | ------------------------- | -------------- |
| POST   | `/api/v1/payments`        | Make payment   |
| POST   | `/api/v1/payments/refund` | Refund payment |

---

## Offer APIs

| Method | Endpoint              | Description    |
| ------ | --------------------- | -------------- |
| GET    | `/api/v1/offers`      | Get all offers |
| POST   | `/api/v1/offers`      | Create offer   |
| PUT    | `/api/v1/offers/{id}` | Update offer   |
| DELETE | `/api/v1/offers/{id}` | Delete offer   |

---

# Booking Workflow

The booking process consists of the following steps:

1. Customer browses movies.
2. Selects city.
3. Selects theatre.
4. Selects show.
5. Views available seats.
6. Selects preferred seats.
7. Discount engine calculates applicable offers.
8. Payment is processed.
9. Booking is created.
10. Seat inventory is updated.
11. Kafka event is published.
12. Booking confirmation is returned.

---

# Booking Sequence Diagram

The following sequence diagram illustrates the complete booking flow.

```
Client
   │
   ▼
Booking Controller
   │
   ▼
Booking Service
   │
   ├── Validate Request
   ├── Validate Seats
   ├── Apply Discounts
   ├── Process Payment
   ├── Save Booking
   ├── Update Seats
   └── Publish Kafka Event
   │
   ▼
Database
```

(Refer to `diagrams/booking-sequence.mmd` and `sequence-booking.png`.)

---

# Booking Cancellation Flow

Cancellation follows these steps:

1. Receive cancellation request.
2. Validate booking.
3. Verify booking status.
4. Refund payment.
5. Release reserved seats.
6. Update seat inventory.
7. Publish Kafka cancellation event.
8. Return success response.

---

# Discount Calculation

The platform currently supports the following promotional offers.

## Offer 1

* 50% discount on every third ticket.

Example:

| Tickets  | Price |
| -------- | ----- |
| 3 × ₹200 | ₹500  |

---

## Offer 2

20% discount on afternoon shows.

Applicable for shows scheduled during the configured afternoon time window.

Discounts are automatically calculated before payment.

---

# Kafka Integration

Kafka enables asynchronous communication between internal components and external systems.

## Published Events

* Booking Created
* Booking Cancelled
* Seat Updated
* Payment Success
* Refund Processed

Benefits:

* Loose coupling
* Event-driven architecture
* Easy integration with theatre systems
* High scalability
* Reliable messaging

---

# Database Design

The project uses MySQL as the primary relational database.

Core entities include:

* Movie
* Theatre
* Screen
* Show
* Seat
* Booking
* BookingSeat
* Payment
* Offer
* User

Relationships are documented in the ER Diagram.

Refer to:

```
diagrams/er-diagram.png
```

and

```
diagrams/er-diagram.mmd
```

---

# System Architecture

The application follows a layered architecture.

```
Client
   │
REST Controller
   │
Service Layer
   │
Repository Layer
   │
MySQL
   │
Kafka
```

See:

```
diagrams/architecture.png
```

---

# Flow Diagram

The flow diagram illustrates the interaction between:

* Customer
* Booking Platform
* Payment Gateway
* Kafka
* Theatre Management System

See:

```
diagrams/flow-diagram.png
```

---

# Deployment Diagram

Deployment consists of the following components.

* Client Applications
* Load Balancer
* Spring Boot Application
* Kafka Broker
* MySQL Database
* External Theatre Systems
* Payment Gateway

Refer to:

```
diagrams/deployment.png
```

---

# Design Patterns Used

The project leverages common enterprise design patterns.

## Repository Pattern

Provides abstraction over database operations.

---

## Dependency Injection

Implemented using Spring Boot's IoC container.

---

## Factory Pattern

Can be extended for supporting multiple payment gateways.

---

## Strategy Pattern

Suitable for implementing multiple discount calculation strategies.

---

## Builder Pattern

Used through Lombok to simplify object construction.

---

# Validation

Input validation is implemented using Jakarta Bean Validation.

Examples include:

* `@NotNull`
* `@NotBlank`
* `@Email`
* `@Positive`
* `@Min`
* `@Max`

This ensures invalid requests are rejected before business logic execution.

---

# Exception Handling

A centralized exception handling mechanism has been implemented using:

* `@ControllerAdvice`
* Custom exception classes
* Consistent API error responses

Handled scenarios include:

* Resource not found
* Invalid input
* Booking failures
* Payment failures
* Seat unavailability
* Database exceptions

---

# Logging

Application logging uses SLF4J with Logback.

Important events logged include:

* Incoming requests
* Booking lifecycle
* Payment processing
* Kafka events
* Errors and exceptions

This improves observability and troubleshooting.

---

# Unit Testing

The project includes unit tests covering:

* Controllers
* Services
* Repositories
* Exception handling
* Mapper classes
* Discount calculation
* Booking workflow

Testing tools:

* JUnit 5
* Mockito
* MockMvc

The objective is to ensure business logic correctness and maintain high code quality.

---

# Docker Support

The application is fully containerized.

Included files:

* Dockerfile
* docker-compose.yml

Services can be started with:

```bash
docker-compose up -d
```

This provides a consistent development and deployment environment across systems.

---

# Scalability Considerations

The application is designed with scalability in mind and can be extended to support enterprise-level traffic.

Key scalability features include:

* Stateless REST APIs
* Layered architecture
* Asynchronous event processing using Kafka
* Database indexing for faster queries
* Horizontal scaling of Spring Boot instances
* Externalized configuration
* Connection pooling
* Pagination support for APIs
* Microservice-ready architecture

Future improvements may include:

* Redis caching
* API Gateway
* Kubernetes deployment
* Distributed tracing
* Read replicas for MySQL
* CQRS for high-volume booking systems

---

# Concurrency Handling

Seat booking is a concurrency-sensitive operation.

To avoid double booking, the application supports:

## Optimistic Locking

Uses version fields to prevent conflicting updates.

Suitable when booking conflicts are relatively infrequent.

---

## Pessimistic Locking

Database-level locking can be enabled for high-contention scenarios.

Ensures only one transaction reserves a seat at a time.

---

## Transaction Management

Booking operations are executed within database transactions.

Typical flow:

1. Validate seat availability.
2. Reserve seats.
3. Process payment.
4. Save booking.
5. Commit transaction.
6. Publish Kafka event.

If any step fails, the transaction is rolled back to maintain consistency.

---

# Security

The application follows common Spring Boot security practices.

Supported features include:

* Spring Security
* JWT Authentication
* Role-based Authorization
* Password Encryption using BCrypt
* Secure REST APIs
* Input Validation
* Exception Handling
* CORS Configuration
* CSRF protection (where applicable)

Possible roles:

* ADMIN
* CUSTOMER
* THEATRE_MANAGER

---

# Performance Optimizations

The following optimizations improve application performance.

## Database

* Proper indexing
* Optimized joins
* Pagination
* Connection pooling

---

## Application

* DTO-based responses
* Lazy loading where appropriate
* Efficient mapping
* Reduced database calls

---

## Kafka

* Asynchronous processing
* Non-blocking notifications
* Event-driven communication

---

## Future Enhancements

* Redis cache
* CDN for movie images
* Elasticsearch
* Async report generation

---

# Monitoring

The application supports monitoring using Spring Boot Actuator.

Useful endpoints include:

| Endpoint            | Purpose          |
| ------------------- | ---------------- |
| `/actuator/health`  | Health status    |
| `/actuator/info`    | Application info |
| `/actuator/metrics` | Metrics          |
| `/actuator/env`     | Environment      |
| `/actuator/loggers` | Logging          |

Monitoring can be integrated with:

* Prometheus
* Grafana
* ELK Stack
* Splunk

---

# Configuration

Application configuration is externalized.

Typical properties include:

```properties
spring.datasource.url=
spring.datasource.username=
spring.datasource.password=

spring.kafka.bootstrap-servers=

spring.jpa.hibernate.ddl-auto=

server.port=

logging.level.root=
```

Profiles supported:

* dev
* test
* prod

---

# CI/CD

A typical deployment pipeline includes:

1. Source Code Commit
2. Maven Build
3. Unit Testing
4. SonarQube Analysis
5. Docker Image Creation
6. Push to Container Registry
7. Deployment
8. Smoke Testing

Popular tools:

* GitHub Actions
* Jenkins
* GitLab CI
* Azure DevOps

---

# Cloud Deployment

The application can be deployed on:

* AWS
* Azure
* Google Cloud Platform

Deployment options include:

* Docker
* Kubernetes
* Virtual Machines
* ECS
* AKS
* GKE

---

# Assumptions

The current implementation assumes:

* Movies already exist in the database.
* Theatres are preconfigured.
* Seat layouts are predefined.
* Payment gateway integration is mocked or simplified.
* Kafka broker is available.
* MySQL database is accessible.
* Users are authenticated before booking.
* Offers are configurable.

---

# Project Structure

```
movie-booking-system
│
├── src
│   ├── main
│   │   ├── java
│   │   ├── resources
│   │   └── ...
│   │
│   └── test
│
├── diagrams
│   ├── architecture.png
│   ├── er-diagram.png
│   ├── booking-sequence.png
│   ├── deployment.png
│   ├── flow-diagram.png
│   └── *.mmd
│
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

---

# Evaluation Mapping

This project demonstrates the following technical capabilities:

| Requirement          | Implemented |
| -------------------- | ----------- |
| Spring Boot          | ✅           |
| REST APIs            | ✅           |
| MySQL                | ✅           |
| Kafka                | ✅           |
| JPA/Hibernate        | ✅           |
| Layered Architecture | ✅           |
| Validation           | ✅           |
| Exception Handling   | ✅           |
| Unit Testing         | ✅           |
| Docker               | ✅           |
| Logging              | ✅           |
| Mermaid Diagrams     | ✅           |
| README Documentation | ✅           |
| Discount Engine      | ✅           |
| Booking Workflow     | ✅           |

---

# Possible Future Enhancements

The platform can be extended with:

* Online seat locking
* Real payment gateway integration
* QR Code ticket generation
* Email notifications
* SMS notifications
* Push notifications
* Movie recommendations
* Loyalty points
* Wallet integration
* Coupon management
* Dynamic pricing
* Admin dashboard
* Theatre dashboard
* Analytics and reporting
* Multi-language support
* Multi-currency support
* Redis caching
* Elasticsearch integration
* Kubernetes deployment
* Distributed tracing with Zipkin/OpenTelemetry

---

# Key Learning Outcomes

This project demonstrates practical implementation of:

* Spring Boot application development
* RESTful API design
* Layered architecture
* Spring Data JPA
* MySQL integration
* Apache Kafka messaging
* Transaction management
* Bean Validation
* Exception handling
* Unit testing using JUnit and Mockito
* Docker containerization
* Mermaid architecture diagrams
* Enterprise software design principles

---

# Conclusion

The Movie Booking System is a complete backend application developed using modern Java and Spring Boot technologies. It demonstrates enterprise-grade architectural practices such as layered design, RESTful APIs, transaction management, asynchronous messaging with Kafka, validation, exception handling, Docker-based deployment, and comprehensive documentation.

The project is structured to be maintainable, scalable, and extensible. It serves as a strong foundation for a production-ready movie ticket booking platform and showcases the practical application of backend engineering concepts commonly used in real-world software development.

---

# Author

**Bharath**

Senior Software Engineer

Technologies:

* Java
* Spring Boot
* Spring Data JPA
* Hibernate
* MySQL
* Apache Kafka
* Docker
* Maven
* JUnit 5
* Mockito
* REST APIs

---

**Thank you for reviewing this project.**
