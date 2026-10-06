# PlacePro – Smart Placement Management System

**PlacePro** is a modern, production-ready college placement management web application built with **Java 17**, **Spring Boot 3**, **Spring Security**, **Spring Data JPA**, **Hibernate**, **Thymeleaf**, **Bootstrap 5**, and **Google Gemini AI API**.

---

## 🚀 Key Features

### 👨‍💼 Administrator Portal
- **Dashboard**: Interactive cards and Chart.js graphs displaying department distribution, status metrics, and company applications.
- **Student CRUD**: Create, read, search, update, delete student profiles with eligibility scores.
- **Company CRUD**: Manage recruiting companies, minimum CGPA criteria, max allowed arrears, and package details.
- **Placement Drive Management**: Schedule placement drives, define deadlines, and close drives.
- **Application Tracking & Shortlisting**: Filter applications by company, department, or status (`APPLIED`, `SHORTLISTED`, `INTERVIEW`, `SELECTED`, `REJECTED`).
- **Reports & CSV Export**: Real-time stats and downloadable CSV reports for college placement records.
- **Admin AI Analyzer**: Run Gemini AI matching and readiness evaluation for any student.

### 🎓 Student Portal
- **Dashboard & Profile**: View personal placement stats, academic CGPA, and edit technical skills/certifications.
- **Available Drives & Eligibility**: Real-time eligibility evaluation (`ELIGIBLE` / `NOT ELIGIBLE` with exact reasons) before applying.
- **My Applications**: Track application statuses in real time.
- **AI Recommendations**: View ranked company matches with skill overlap percentages and match reasons.
- **Skill Gap Analysis**: Visual breakdown of matched vs missing skills + AI learning roadmap.
- **Placement Readiness**: Weighted readiness score (`HIGH`, `MEDIUM`, `LOW`) with transparent calculation breakdown.

---

## 🛠️ Technology Stack

| Layer | Technology |
|---|---|
| **Backend Framework** | Java 17+, Spring Boot 3.2.5, Spring MVC |
| **Security** | Spring Security 6 (BCrypt Password Hashing, Role-based Access) |
| **Persistence** | Spring Data JPA, Hibernate ORM |
| **Database** | MySQL 8.0+ (Production) / H2 in MySQL mode (Zero-setup Fallback) |
| **Frontend** | Thymeleaf, HTML5, CSS3, JavaScript, Bootstrap 5, Chart.js, Bootstrap Icons |
| **AI Integration** | Google Gemini API (`gemini-2.5-flash`) + `LocalRecommendationEngine` fallback |
| **Build Tool** | Apache Maven 3.9+ |

---

## ⚙️ Environment Variables & Configuration

The application uses environment variables with safe default fallbacks:

| Variable | Description | Default / Fallback |
|---|---|---|
| `DB_URL` | JDBC Database Connection URL | `jdbc:h2:mem:placepro;MODE=MySQL;DB_CLOSE_DELAY=-1` |
| `DB_USERNAME` | Database Username | `sa` |
| `DB_PASSWORD` | Database Password | *(empty)* |
| `GEMINI_API_KEY` | Google Gemini API Key for AI features | *(empty - triggers Local Engine)* |

> **Note**: If `GEMINI_API_KEY` is omitted, PlacePro automatically falls back to `LocalRecommendationEngine.java` without crashing.

---

## 💻 How to Run in IntelliJ IDEA

1. **Open Project**:
   - Open IntelliJ IDEA -> `File` -> `Open...` -> Select `placepro-ai/pom.xml`.
2. **Maven Sync**:
   - Wait for IntelliJ to download dependencies listed in `pom.xml`.
3. **Set Environment Variables (Optional)**:
   - Go to `Run` -> `Edit Configurations...` -> `PlaceProApplication`.
   - Set environment variables if connecting to MySQL or Gemini API:
     ```env
     DB_URL=jdbc:mysql://localhost:3306/placepro
     DB_USERNAME=root
     DB_PASSWORD=your_password
     GEMINI_API_KEY=your_gemini_api_key
     ```
4. **Run Application**:
   - Run `src/main/java/com/placepro/PlaceProApplication.java`.
   - Open browser at `http://localhost:8080`.

---

## 🔑 Demonstration Credentials

| Role | Username | Password |
|---|---|---|
| **Administrator** | `admin` | `admin123` |
| **Student (CSE)** | `student1` | `student123` |
| **Student (CSE)** | `student2` | `student123` |
| **Student (ECE)** | `student3` | `student123` |
| **Student (IT)** | `student4` | `student123` |
| **Student (High CGPA)** | `student6` | `student123` |

---

## 🧪 Running Tests

Execute Maven test command:
```bash
./mvnw clean test
```
This runs context loading, student CRUD, company CRUD, eligibility evaluation, duplicate application prevention, and local AI fallback tests.
