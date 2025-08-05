# Spring Boot + Elasticsearch - Course Search API

This project implements a Course Search API using **Spring Boot** and **Elasticsearch**.  
It supports **filtering**, **pagination**, **sorting**, and **updating course details** with a simple HTML UI.

---

## Features
- **Bulk index** sample data (50+ courses) into Elasticsearch
- **Search with filters** (category, type, age range, price range)
- **Pagination & sorting** (upcoming date, price)
- **Edit & update courses**
- **Simple HTML UI** to search, filter, paginate, and edit

---

## Technologies Used
- **Java 17+**
- **Spring Boot 3.x**
- **Spring Data Elasticsearch**
- **Elasticsearch 8.x** (Docker)
- **HTML, CSS, JavaScript**

---

## Project Structure
```plaintext
springboot-elasticsearch/
├── docker-compose.yml
├── pom.xml
├── README.md
├── src/main/java/com/example/elasticsearchdemo/
│   ├── controller/
│   │   └── SearchController.java
│   ├── document/
│   │   └── CourseDocument.java
│   ├── repository/
│   │   └── CourseRepository.java
│   ├── service/
│   │   └── SearchService.java
│   └── ElasticsearchDemoApplication.java
├── src/main/resources/
│   ├── application.properties
│   ├── sample-courses.json
│   └── static/index.html


 Setup Instructions
1. Start Elasticsearch
Make sure you have Docker installed. Use the provided docker-compose.yml to start an Elasticsearch instance.

```bash

docker-compose up -d
Verify that Elasticsearch is running:

```bash

curl http://localhost:9200

2. Build & Run Spring Boot Application
Use Maven to build and run the application.

```bash

mvn clean install
mvn spring-boot:run

3. Access the Application
API Base URL → http://localhost:8080/api/search

UI → Open src/main/resources/static/index.html in your browser


## 📺 Demo Video
[Watch on Google Drive](https://drive.google.com/file/d/1C6tU5XtfJwKOC9FHZzde5fk3h6Ik2NKG/view?usp=drive_link)

