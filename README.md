# Nagrik Vani - Civic Issue Reporting System

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://www.java.com)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

Nagrik Vani is a modern, cloud-native microservices application designed to streamline the process of reporting and resolving civic issues. It provides a platform for citizens to raise complaints and for municipal departments to manage, assign, and resolve these issues efficiently.

## Architecture Overview

The system is built on a distributed, event-driven microservices architecture to ensure scalability, resilience, and maintainability.

**Core Infrastructure:**
* **Service Registry (Eureka):** For dynamic service discovery.
* **Config Server:** Centralized configuration management for all services, backed by a Git repository. View configuration files here: https://github.com/SanskarPatidar/Sih-Config-Server-Files
* **API Gateway:** A single entry point for all client requests, handling routing, security, and cross-cutting concerns.
* **Message Broker (Kafka):** For asynchronous communication between services, ensuring decoupling and fault tolerance.

**Application Services:**
* `auth-service`: Manages user authentication and authorization.
* `citizen-service`: Manages citizen profiles.
* `complaint-service`: Handles the creation and lifecycle of complaints.
* `department-admin-service`: Provides functionality for department administrators.
* `department-staff-service`: Provides functionality for department staff.
* `issue-service`: Aggregates complaints into actionable issues.

**Observability Stack:**
* **Prometheus:** For collecting time-series metrics from all services.
* **Grafana:** For visualizing metrics and creating monitoring dashboards.
* **Zipkin:** For distributed tracing to monitor request latency across the microservice landscape.

**Key Highlights:**
* **Centralized API Contracts with a Shared Client Library:** Engineered a dedicated Maven module to act as a single source of truth for all Data Transfer Objects (DTOs), Exceptions and enums. This enforces a consistent data model across the system, prevents data model drift between services, and enables type-safe inter-service communication.

  View library here: https://github.com/SanskarPatidar/Sih-Microservice-Client-Library
* **Broker-Agnostic Event-Driven Architecture:** Engineered the messaging layer using Spring Cloud Stream's binder abstraction, decoupling the business logic from the underlying message broker (Kafka). This ensures the core application code is fully portable and enables a future migration to alternatives like RabbitMQ with only configuration-level changes.
* **Optimized Docker Image Build Process:** Engineered a reusable, multi-stage Dockerfile for the multi-module architecture, leveraging advanced layer caching and Spring Boot's layertools. This optimization reduced subsequent build times by over 80% and decreased final image size by over 60%, significantly improving CI/CD pipeline efficiency and developer workflow.
* **CI/CD pipeline with GitHub Actions:** Efficient workflows to automate the building, testing, and containerization of the entire multi-module microservices application, publishing versioned Docker images to Docker Hub. This process ensures every commit is automatically validated, resulting in consistent, deployable artifacts and reducing manual deployment efforts.

**Other Features:**
* **Comprehensive Security:** Implemented a token-based security model using JWT and OAuth2 for securing all API endpoints and enabling stateless inter-service communication.
* **Advanced API Management:** Leveraged an API Gateway to provide centralized routing, client-side load balancing (integrated with Eureka), and rate limiting to protect services from overload.
* **ACID Transaction Management:** Ensured data integrity and consistency for all database operations by implementing declarative transactions with Spring's `@Transactional` annotation.
* **Dynamic and Type-Safe Querying:** Built complex, dynamic database queries for MongoDB programmatically using the Spring Data Criteria API, avoiding raw JSON query strings and preventing NoSQL injection vulnerabilities.
* **Efficient Data Retrieval:** Implemented pagination across all relevant API endpoints to ensure fast and efficient handling of large data sets.
* **Centralized Exception Handling:** Created a robust error handling strategy using `@ControllerAdvice` to provide consistent and meaningful error responses across the entire application.
* **Comprehensive API Documentation:** Generated interactive and standardized API documentation for all microservices using OpenAPI 3 (Swagger).

## Tech Stack

* **Backend:** Java 21, Spring Boot 3.5.x, Spring Cloud, Spring Cloud Stream
* **Databases:** MongoDB, Redis
* **Messaging:** Apache Kafka (running in KRaft mode)
* **Build & Deployment:** Maven, Docker, Docker Compose
* **Observability:** Prometheus, Grafana, Zipkin

## Prerequisites

Before you begin, ensure you have the following installed on your machine:
* [Git](https://git-scm.com/downloads)
* [Docker Desktop](https://www.docker.com/products/docker-desktop/) (make sure it's running)
* An IDE like IntelliJ IDEA is recommended.

## Quick Start: Running the Entire System

Follow these steps to get the entire application running on your local machine.

### 1. Clone the Repository
Open your terminal and clone the project:
```git
git clone https://github.com/SanskarPatidar/nagrik-vani.git
cd nagrik-vani
```

### 2\. Create the Environment Configuration (`.env` file)

This is the most important step. You need to create a local `.env` file to hold all the necessary configuration and secrets.

1.  In the root of the project (`nagrik-vani/`), create a new file named `.env`.

2.  Copy and paste the entire content below into your new `.env` file.

    ```ini
    MONGO_URI=mongodb://root:password@mongo:27017/nagrikvani?authSource=admin
    REDIS_HOST=redis
    REDIS_PASSWORD=password
    REDIS_PORT=6379
    EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE=http://service-registry:8761/eureka
    SPRING_CONFIG_SERVER_CONNECTION=http://config-server:8888 # SPRING_CONFIG_IMPORT name is dangerous, do not try this at home
    DOCKER_USERNAME=sanskarpatidar
    ZIPKIN_CONNECTION_URL=http://zipkin:9411/api/v2/spans
    JWT_SECRET_KEY=your-super-secret-jwt-key-that-is-long-and-secure
    KAFKA_CONNECTION=kafka:29092
    
    ```

### 3\. Build and Run with Docker Compose

Now, from the root directory of the project, run this single command. It will build all the microservice images and start all containers in the correct order.

```bash
docker-compose up --build -d
```

The first time you run this, it will take several minutes to download base images and build all your services. Subsequent builds will be much faster.

### 4\. Verify that Everything is Running

Wait a minute or two for all services to start up. You can check the status of all containers with:

```bash
docker-compose ps
```

All services should have a `Status` of `Up` or `running (healthy)`. You can manually start/stop services on docker desktop

---

## Accessing Services & Dashboards

Once all services are running, you can access the various parts of the system from your browser. The API Gateway at port `8080` is the primary entry point for all application APIs.

| Service | Local URL | Description |
| :--- | :--- | :--- |
| **Aggregated API Docs** | `http://localhost:8080/docs` | **Centralized Swagger UI for all microservices, accessible through the API Gateway.** |
| **API Gateway** | `http://localhost:8080` | The main entry point for all client requests. |
| **Service Registry**| `http://localhost:8761` | Eureka dashboard showing all registered services. |
| **Config Server** | `http://localhost:8888` | Spring Cloud Config Server. |
| **Grafana** | `http://localhost:3000` | Dashboards for monitoring. (Login: `admin`/`admin`) |
| **Prometheus** | `http://localhost:9090` | Metrics collection server. (Check `Status > Targets`) |
| **Zipkin** | `http://localhost:9411` | Distributed tracing UI. |

## Stopping the Application

To stop and remove all running containers, networks, and volumes, run:

```bash
docker-compose down -v
```
```
