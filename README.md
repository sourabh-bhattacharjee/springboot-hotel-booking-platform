# 🏨 Hotel Booking Platform – Spring Boot Backend

A production-ready hotel booking backend system inspired by various hotel booking platforms, built using Spring Boot, PostgreSQL, and AWS (EC2 + RDS).
The application supports hotel management, room inventory, dynamic pricing, secure bookings, and payment integration.

🔗 **Live API (Swagger UI):**

https://sourabhbhattacharjee.net/hotel_booking/swagger-ui/index.html

📐 **System Design Diagram:**

<img width="4936" height="3566" alt="image" src="https://github.com/user-attachments/assets/73d26ea4-8ad4-4e70-a643-3225fedaf555" />

### 🚀 Key Features

####  User & Authentication

    - JWT based authentication and authorization

    - Role based access control (Admin / Owner / User)

    - Secure password handling

#### Hotel & Room Management

    - Hotel creation and activation by owners

    - Room management with capacity, amenities, and photos

    - Role restricted admin operations

#### Inventory & Pricing

    - Daily room inventory tracking

    - Dynamic pricing engine with:

        - Base price

        - Surge pricing

        - Occupancy based pricing

        - Holiday pricing

        - Urgency pricing

    - Automatic price recalculation on base price updates


#### Booking System

    - Date range availability checks

    - Multi guest bookings

    - Booking lifecycle management (created, confirmed, cancelled)

    - Inventory consistency enforcement
    

### 🛠️ Tech Stack


#### Backend

    - Java 21

    - Spring Boot

    - Spring Data JPA

    - Spring Security (JWT)

    - Hibernate

#### Database

    - PostgreSQL

    - AWS RDS (production)

    - Optimized schema with relational integrity

#### Cloud & DevOps

    - AWS EC2 (application hosting)

    - AWS RDS (managed PostgreSQL)

    - Environment-variable based configuration

    - GitHub for version control

#### Tools

    - Swagger / OpenAPI

    - Maven

    - DBeaver / psql
  

### 🔐 Configuration & Secrets

All sensitive values are managed using environment variables, making the application portable across environments.
``` 
DB_URL=jdbc:postgresql://<rds-endpoint>:5432/airBnb
DB_USERNAME=postgres
DB_PASSWORD=********

JWT_SECRET_KEY=********
STRIPE_SECRET_KEY=********
STRIPE_WEBHOOK_SECRET=********

```

No secrets are committed to the repository.


### 📦 Deployment

    - Backend deployed on AWS EC2

    - Database hosted on AWS RDS (PostgreSQL)

    - Secure networking using Security Groups

    - Public API access via HTTPS

    - Database access restricted to EC2 only


### 👤 Author

Sourabh Bhattacharjee </br>
Backend Engineer | Java | Spring Boot | AWS
