# Movie Ticket Booking Platform - Architecture

## System Overview

### 10 Microservices
1. **API Gateway** - Request routing & authentication
2. **Movie Catalogue** - Movies management
3. **Theatre Service** - Theatre operations
4. **Show Service** - Show scheduling
5. **Booking Service** - Core booking logic
6. **Payment Service** - Payment processing
7. **Offer Service** - Promotional offers
8. **Inventory Service** - Seat management
9. **Notification Service** - Alerts & emails
10. **Analytics Service** - Reporting

## Technology Stack
- Java 11+, Spring Boot 2.7
- MySQL, MongoDB, Redis
- Apache Kafka, Docker, Kubernetes
- Prometheus, Grafana, ELK Stack

## Key Features
- High-concurrency booking with distributed locks
- Real-time seat inventory sync
- Event-driven architecture
- Payment gateway integration
- Dynamic pricing & offers
- Comprehensive monitoring

## Setup
```bash
docker-compose up -d
mvn clean install
mvn spring-boot:run