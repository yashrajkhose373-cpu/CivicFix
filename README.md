# 🎓 StudySync — Student Productivity Platform

> A Java Spring Boot student productivity platform for managing tasks, assignments, deadlines, subjects, and study notes.

---

## 🌟 Overview

**StudySync** solves student workflow fragmentation by providing a unified, real-time productivity hub. Built on clean **Java 21** and **Spring Boot 3.3** architecture, it offers full lifecycle management for coursework, deadlines, subjects, study cheat sheets, and priority matrices.

---

## 🚀 Key Features

* 📊 **Interactive Analytics Dashboard**: Real-time KPI cards tracking total tasks, pending/in-progress counts, completion rates, upcoming deadlines, and course workload charts.
* 📝 **Task & Assignment Management**: Comprehensive CRUD operations for tasks with priorities (`LOW`, `MEDIUM`, `HIGH`, `URGENT`), status workflows (`PENDING`, `IN_PROGRESS`, `COMPLETED`), and automated overdue detection.
* 📂 **Subject & Course Organization**: Link tasks and notes to custom subjects with color codes, course IDs, and instructor information.
* ⏱️ **Deadlines & Overdue Tracker**: Dedicated timeline view separating overdue assignments from upcoming milestones.
* 📓 **Study Notes & Cheat Sheets**: Rich study notes with tags, markdown support, and pinning to the dashboard.
* 🔍 **Search & Multi-Dimensional Filtering**: Filter instantly by keyword, priority, status, subject, or task type.
* 💾 **Permanent MySQL Persistence**: Production-grade schema with automated table creation, foreign key constraints, indexes, and sample seed data.

---

## 🛠️ Tech Stack

* **Backend**: Java 21 LTS, Spring Boot 3.3.4 (Spring Web, Spring Data JPA, Bean Validation, Lombok)
* **Database**: MySQL 8.x (with H2 Database fallback profile for quick evaluation)
* **Frontend**: Vanilla HTML5, Modern CSS3 (Dark Mode, Glassmorphism, Micro-animations), JavaScript ES6+
* **Build Tool**: Apache Maven

---

## 📁 Project Architecture

```text
studysync/
├── src/
│   ├── main/
│   │   ├── java/com/studysync/
│   │   │   ├── config/             # DataInitializer (Sample Data), WebMvcConfig
│   │   │   ├── controller/         # REST Controllers (Task, Note, Subject, Dashboard)
│   │   │   ├── dto/                # Request & Response DTOs, ApiResponse wrapper
│   │   │   ├── entity/             # JPA Entities (Task, Note, Subject, Enums)
│   │   │   ├── exception/          # GlobalExceptionHandler, Custom Exceptions
│   │   │   ├── repository/         # Spring Data JPA Repositories & JPQL queries
│   │   │   ├── service/            # Business Logic Layer
│   │   │   └── StudySyncApplication.java
│   │   └── resources/
│   │       ├── static/             # Frontend Client (index.html, css/styles.css, js/app.js)
│   │       ├── application.yml     # MySQL Database Configuration
│   │       └── application-dev.yml # H2 In-Memory Profile
│   └── test/                       # Spring Boot Test Suite
├── .env.example                    # Environment Variable Template
├── .gitignore                      # Git Ignore Configuration
├── pom.xml                         # Maven Dependencies & Build Configuration
└── README.md                       # Documentation & API Reference
```

---

## 🔌 REST API Documentation

### 📌 Tasks & Assignments API
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/tasks` | Get all tasks (supports query params: `search`, `status`, `priority`, `type`, `subjectId`) |
| `GET` | `/api/tasks/{id}` | Get single task by ID |
| `POST` | `/api/tasks` | Create new task / assignment |
| `PUT` | `/api/tasks/{id}` | Update existing task |
| `PATCH` | `/api/tasks/{id}/toggle` | Toggle completion status |
| `DELETE` | `/api/tasks/{id}` | Delete task |

### 📌 Study Notes API
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/notes` | Get all notes (supports `search`, `subjectId`) |
| `GET` | `/api/notes/{id}` | Get note details by ID |
| `POST` | `/api/notes` | Create new note |
| `PUT` | `/api/notes/{id}` | Update note |
| `PATCH` | `/api/notes/{id}/pin` | Pin/Unpin note on dashboard |
| `DELETE` | `/api/notes/{id}` | Delete note |

### 📌 Subjects & Courses API
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/subjects` | List all subjects with task/note statistics |
| `GET` | `/api/subjects/{id}` | Get subject by ID |
| `POST` | `/api/subjects` | Add new subject / course |
| `PUT` | `/api/subjects/{id}` | Update subject |
| `DELETE` | `/api/subjects/{id}` | Delete subject |

### 📌 Dashboard & Analytics API
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/dashboard` | Get real-time statistics, KPIs, upcoming/overdue tasks, pinned notes |

---

## ⚡ Getting Started & Setup Instructions

### 1. Prerequisites
* **Java 17 or Java 21+**
* **Maven 3.8+**
* **MySQL 8.0+** (Optional if running with dev profile)

### 2. Configure Database
Create a MySQL database:
```sql
CREATE DATABASE studysync_db;
```

Update your database credentials in `src/main/resources/application.yml` or export environment variables:
```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/studysync_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=your_password
```

*(Alternatively, run with `--spring.profiles.active=dev` to use the built-in in-memory H2 database).*

### 3. Build and Run
```bash
# Build the project
mvn clean package

# Run the Spring Boot application
mvn spring-boot:run
```

### 4. Access Application
Open your browser and navigate to:
```text
http://localhost:8080
```
The application will automatically load with sample data pre-seeded.

---

## 📜 License
MIT License. Built for students worldwide.
