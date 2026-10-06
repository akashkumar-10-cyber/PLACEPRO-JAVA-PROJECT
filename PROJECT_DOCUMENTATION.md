# PlacePro: Smart Placement Management System
## Academic Project Documentation & Technical Report

---

### 1. Introduction & Executive Summary
**PlacePro** is an AI-assisted college placement management web application engineered using the Java Spring Boot ecosystem. It replaces traditional manual placement workflows with automated eligibility checks, real-time application tracking, interactive statistical reporting, and artificial intelligence capabilities powered by Google Gemini API and a local fallback scoring engine.

---

### 2. Problem Statement
Traditional placement management in colleges suffers from:
1. **Manual Eligibility Verification**: Verification of student CGPA and arrears against company criteria is error-prone and slow.
2. **Lack of Personalization**: Students struggle to identify which companies best fit their skill sets.
3. **Unidentified Skill Gaps**: Students are often unaware of specific missing technical skills required by target employers.
4. **Scattered Records**: Lack of centralized role-based application tracking for placement officers and students.

---

### 3. Proposed Solution
PlacePro solves these challenges by providing:
- **Automated Eligibility Engine**: Instant checks of CGPA, standing arrears, and deadlines before allowing application submission.
- **AI Recommendation Engine**: Match score calculations comparing student profiles (CGPA, aptitude, communication, skills, projects) against company requirements.
- **AI Skill Gap Analysis**: Highlighting matched vs. missing skills and providing actionable learning roadmaps.
- **Placement Readiness Index**: Transparent 0–100 weighted readiness rating (`HIGH`, `MEDIUM`, `LOW`).
- **Role-Based Access Control**: Separate secure portals for Administrators and Students.

---

### 4. System Architecture

```
[ Web Browser / Thymeleaf UI + Bootstrap 5 ]
                     │
                     ▼
       [ Spring MVC Controllers ]
                     │
                     ▼
         [ Service Layer & AI ]
     ┌───────────────┼───────────────┐
     ▼               ▼               ▼
[Gemini API]  [Local Engine] [Data Services]
                     │
                     ▼
          [ Spring Data JPA ]
                     │
                     ▼
         [ MySQL / H2 Database ]
```

---

### 5. Database Schema & ER Design
The database consists of six core entities:
1. **users**: Authentication data with BCrypt encrypted passwords and roles (`ROLE_ADMIN`, `ROLE_STUDENT`).
2. **students**: Personal, academic, skill, aptitude, and communication scores linked to a user.
3. **companies**: Company details, salary package (LPA), minimum CGPA, maximum arrears, and required skills.
4. **placement_drives**: Job role postings, drive dates, application deadlines, and statuses (`OPEN`, `CLOSED`).
5. **applications**: Student drive submissions with unique constraint `(student_id, placement_drive_id)` and status tracking.
6. **recommendations**: Persisted AI match scores and match explanations.

---

### 6. AI Methodology & Scoring Algorithm
PlacePro uses a two-tier recommendation architecture:

#### Local Transparent Scoring Formula:
$$\text{Match Score} = S_{\text{Skill}} (50\%) + S_{\text{CGPA}} (20\%) + S_{\text{Aptitude}} (15\%) + S_{\text{Comm}} (10\%) + S_{\text{Portfolio}} (5\%)$$

- **Skill Overlap ($50\%$)**: Ratio of matched student skills to total company required skills.
- **CGPA Fit ($20\%$)**: Ratio of student CGPA to company minimum requirement.
- **Aptitude Score ($15\%$)**: Normalized score out of 100.
- **Communication Score ($10\%$)**: Normalized score out of 100.
- **Certifications & Projects ($5\%$)**: Count of relevant portfolio projects and certifications.

#### AI Enhancement Layer (Gemini API):
When `GEMINI_API_KEY` is provided, PlacePro passes the student profile and match metrics to Google Gemini (`gemini-2.5-flash`) to generate natural language match reasons, personalized skill development roadmaps, and readiness evaluations. If the API is unavailable, `LocalRecommendationEngine.java` seamlessly fulfills the request.

---

### 7. Technologies Used
- **Language**: Java 17
- **Framework**: Spring Boot 3.2.5
- **Security**: Spring Security 6, BCrypt
- **ORM & Database**: Spring Data JPA, Hibernate 6, MySQL 8.0, H2 Database
- **Template Engine**: Thymeleaf with Spring Security 6 dialect
- **Frontend**: HTML5, CSS3, JavaScript, Bootstrap 5, Chart.js, Bootstrap Icons
- **AI Integration**: Google Gemini REST API

---

### 8. Testing & Validation Summary
Automated unit and integration tests were executed covering:
- **Authentication**: Security filter chain validation.
- **Student CRUD**: Creation, search by department/CGPA, updating, and deletion.
- **Company CRUD**: Company registration and criterion updates.
- **Eligibility Engine**: Correct rejection of students with low CGPA or standing arrears.
- **Application Logic**: Prevention of duplicate applications.
- **AI Fallback**: Verification that recommendations execute correctly without an API key.

---

### 9. Future Enhancements
- Integration of resume PDF parser for automated skill extraction.
- Automated email/SMS notifications for upcoming drive deadlines.
- Mock interview scheduling module with video assessment.
