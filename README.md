\# 🛒 WCL Marketplace



!\[Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)

!\[Spring Boot](https://img.shields.io/badge/Spring\_Boot-3.2-brightgreen?logo=springboot)

!\[Angular](https://img.shields.io/badge/Angular-17-red?logo=angular)

!\[Kafka](https://img.shields.io/badge/Apache\_Kafka-3.6-black?logo=apachekafka)

!\[Docker](https://img.shields.io/badge/Docker-Containerized-blue?logo=docker)



An event-driven, reactive microservices e-commerce platform designed to demonstrate modern distributed system patterns. 



This project showcases a production-ready monorepo featuring real-time inventory synchronization across multiple clients using Server-Sent Events (SSE), reactive streams (Project Reactor), and Kafka message brokering.



\---



\## 📋 Table of Contents

\- \[Architecture Overview](#-architecture-overview)

\- \[The Event-Driven Flow](#-the-event-driven-flow)

\- \[Tech Stack](#-tech-stack)

\- \[Project Structure](#-project-structure)

\- \[Getting Started (Local Development)](#-getting-started-local-development)

\- \[Testing the Reactive Real-Time UI](#-testing-the-reactive-real-time-ui)

\- \[Troubleshooting](#-troubleshooting)

\- \[Roadmap](#-roadmap)



\---



\## 🏗️ Architecture Overview



The platform uses a polyglot microservices architecture. All external traffic is routed through a central Spring Cloud API Gateway, which handles routing and JWT-based authentication before proxying requests to isolated domain services. Every service manages its own database, adhering to the database-per-service pattern.



\### Core Microservices

\* \*\*API Gateway (`/marketplace-infra`):\*\* Nginx-proxied Spring Cloud Gateway acting as the single entry point.

\* \*\*Product Service (`/marketplace-backend`):\*\* Spring WebFlux backend managing the product catalog in \*\*MongoDB\*\*. It consumes purchase events and broadcasts reactive SSE streams.

\* \*\*Order Service (`/marketplace-backend`):\*\* Spring Boot service managing transactional order data in \*\*PostgreSQL\*\*. Emits domain events to Kafka upon order creation.

\* \*\*User Service (`/marketplace-backend`):\*\* Authentication and identity management service.

\* \*\*Frontend (`/marketplace-frontend`):\*\* Angular Single Page Application (SPA) utilizing RxJS for reactive state management and `EventSourcePolyfill` for authenticated SSE streams.



\---



\## ⚡ The Event-Driven Flow



To demonstrate true reactive microservices, this application features a real-time inventory synchronization loop:



1\. \*\*Trigger:\*\* A user places an order via the Angular UI.

2\. \*\*Transaction:\*\* The \*\*Order Service\*\* saves the transaction to PostgreSQL and publishes an `OrderCreated` event to \*\*Apache Kafka\*\*.

3\. \*\*Consumption:\*\* The \*\*Product Service\*\* consumes the Kafka event and decrements the inventory in MongoDB.

4\. \*\*Broadcast:\*\* The Product Service pushes the updated Product DTO into a Reactive Sink (`Flux`).

5\. \*\*Stream:\*\* The API Gateway streams this event via SSE to all connected Angular clients.

6\. \*\*UI Reactivity:\*\* The Angular UI updates instantly across all active browser windows without a page refresh.



\---



\## 💻 Tech Stack



\*\*Frontend\*\*

\* Angular 17, TypeScript, RxJS

\* Bootstrap / SCSS

\* EventSource Polyfill (for Authenticated SSE)



\*\*Backend\*\*

\* Java 21, Spring Boot 3.x

\* Spring WebFlux \& Project Reactor

\* Spring Cloud Gateway



\*\*Infrastructure \& Data\*\*

\* Apache Kafka \& ZooKeeper

\* MongoDB (Product Catalog)

\* PostgreSQL (Orders \& Users)

\* Docker \& Docker Compose



\---



\## 📂 Project Structure



```text

wcl-marketplace/

├── marketplace-frontend/      # Angular SPA UI

├── marketplace-backend/       # Java Domain Services

│   ├── api-gateway/           # Spring Cloud Gateway

│   ├── user-service/          # Auth \& Identity

│   ├── product-service/       # WebFlux \& MongoDB

│   └── order-service/         # MVC \& PostgreSQL

├── marketplace-infra/         # Infrastructure config

│   └── docker/

│       └── docker-compose.yml # Main cluster configuration

└── .gitignore                 # Global ignore file



\------



🚀 Getting Started (Local Development)

The entire architecture is containerized. You do not need Java, Node.js, or any databases installed locally to run this project—only Docker.



Prerequisites

Docker Desktop installed and running.



Git installed.



1\. Clone the Repository

Bash

\~git clone \[https://github.com/yourusername/wcl-marketplace.git](https://github.com/yourusername/wcl-marketplace.git)

\~cd wcl-marketplace



2\. Boot the Cluster

Navigate to the infrastructure directory and spin up the Docker containers. The --build flag ensures all Java and Angular images are compiled fresh.

Bash

\~cd marketplace-infra/docker

\~docker compose up -d --build

3\. Verify Startup

Because this is a large microservices cluster, Kafka, Zookeeper, and the databases need about 30-60 seconds to initialize before the Java services fully start.



You can monitor the health of the core services using:

Bash

\~docker ps

\# OR view specific logs:

\~docker logs wcl-product-service -f

Once all containers show as running/healthy, the application is available at:

👉 http://localhost



\---



🧪 Testing the Reactive Real-Time UI

To experience the Kafka + Server-Sent Events architecture in action:



* Open a web browser and navigate to http://localhost.
* Open a second, entirely separate browser window (or an Incognito window) and navigate to the same URL. Snap them side-by-side.
* Ensure you are authenticated in both windows.
* In Window A, click the button to purchase an item.
* Keep your eyes on Window B. The inventory number will instantly drop in the second window without any page refresh or manual intervention.



\---



🛠️ Troubleshooting

* Port Conflicts: If docker compose up fails, ensure ports 80, 8080, 5432, 27017, and 9092 are not being used by other local applications.
* Insufficient Docker Memory: This cluster runs 4 Java Spring Boot applications, Angular, Nginx, Kafka, Zookeeper, Mongo, and Postgres simultaneously. Ensure Docker Desktop is allocated at least 4GB to 8GB of RAM in its engine settings.
* Submodule Issues: If the backend or frontend folders appear empty, ensure you did not accidentally initialize nested Git repositories.



\---



🛑 Teardown

To stop the cluster and remove the containers, but keep your database data:



Bash

\~cd marketplace-infra/docker

\~docker compose down

To completely wipe the cluster and delete all database volumes (factory reset):



Bash

\~docker compose down -v

