# 🎬 Movie Booking Application

A full-stack Movie Booking Application that enables users to browse movies, search for available shows, book tickets, and manage bookings. The application also provides administrative capabilities to monitor ticket availability and update movie booking status.

## 🚀 Features

### User Features

* User Registration and Authentication
* Secure Login and Password Management
* Browse Available Movies
* Search Movies by Name
* View Theatre Information
* Book Movie Tickets
* View Booking Details

### Admin Features

* View Booked Tickets
* Monitor Ticket Availability
* Update Ticket Status
* Manage Movie Inventory

---

## 🏗️ Architecture

### Frontend

* Angular
* HTML5
* CSS3
* TypeScript

### Backend

* Java
* Spring Boot
* Spring Data MongoDB
* REST APIs

### Database

* MongoDB

### Messaging

* Apache Kafka

### Monitoring & Logging

* Spring Boot Actuator
* Prometheus
* Logstash

### DevOps & Quality

* Docker
* Maven
* JUnit
* Mockito
* Git

---

## 📌 Key Functionalities

### Authentication Module

* User Registration
* User Login
* Password Reset
* Logout Functionality

### Movie Module

* View All Movies
* Search Movies
* View Theatre Details

### Ticket Booking Module

* Book Tickets
* Select Number of Seats
* Track Booking Information

### Admin Module

* View Ticket Statistics
* Update Ticket Availability
* Manage Booking Status

---

## 📂 Project Structure

```text
movie-booking-app/
│
├── frontend/
│   ├── Angular Application
│
├── backend/
│   ├── Controllers
│   ├── Services
│   ├── Repositories
│   ├── Models
│   ├── DTOs
│   └── Configurations
│
├── docker/
├── kafka/
├── monitoring/
└── docs/
```

## 🔌 REST APIs

| Method | Endpoint                   | Description          |
| ------ | -------------------------- | -------------------- |
| POST   | /register                  | Register User        |
| POST   | /login                     | User Login           |
| GET    | /movies                    | Get All Movies       |
| GET    | /movies/search/{movieName} | Search Movie         |
| POST   | /movies/{movieName}/book   | Book Tickets         |
| PUT    | /movies/{movieName}/update | Update Ticket Status |

---

## 🛠️ Installation

### Prerequisites

* Java 17+ (or your version)
* Maven
* MongoDB
* Apache Kafka
* Node.js
* Angular CLI
* Docker (Optional)

### Backend Setup

```bash
git clone https://github.com/<your-username>/movie-booking-app.git

cd backend

mvn clean install

mvn spring-boot:run
```

### Frontend Setup

```bash
cd frontend

npm install

ng serve
```

Application will be available at:

```text
http://localhost:4200
```

Backend APIs:

```text
http://localhost:8080
```

---

## 🧪 Testing

Run backend tests:

```bash
mvn test
```

Run frontend tests:

```bash
ng test
```

---

## 📈 Monitoring

Application monitoring is implemented using:

* Spring Boot Actuator
* Prometheus

Key metrics include:

* API Health
* JVM Metrics
* Request Statistics
* Application Performance

---

## 🐳 Docker Support

Build Docker Image:

```bash
docker build -t movie-booking-app .
```

Run Container:

```bash
docker run -p 8080:8080 movie-booking-app
```

---

## ☁️ Future Enhancements

* Online Payment Integration
* Seat Layout Visualization
* Email Notifications
* Movie Recommendations
* Cloud Deployment
* Role-Based Access Control (RBAC)
---

## 👩‍💻 Author

Poornima Mogaveera

Software Engineer | Java | Spring Boot | MongoDB | Angular | Cloud Technologies

---

## ⭐ If you found this project useful, please consider giving it a star.
