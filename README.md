CPCL SPARC Sports Scholarship Portal

📌 Project Overview

The SPARC Portal is a full-stack enterprise web application built to digitize the sports scholarship application process. It replaces manual workflows with a secure, centralized digital pipeline featuring stateless authentication, dynamic data validation, multipart file uploads, and a real-time administrative quota-tracking dashboard.

✨ Core Features & Implementation Details

🛡️ Authentication & Security

Stateless JWT: Login generates a JSON Web Token stored in localStorage.

Role-Based Routing: The React router dynamically directs ADMIN users to the dashboard and standard users to the /apply or /upload pipelines.

API Protection: Spring Boot AuthTokenFilter intercepts requests to ensure only admins can hit /api/admin/* endpoints.

🏃 Applicant Module (React Frontend)

Dynamic Validation: Uses Formik & Yup for strict client-side validation. Cascading dropdowns dynamically validate achievement based on the selected scholarshipLevel (Elite Scholar vs. Scholar) and tournamentLevel.

Client-Side PDF Generation: Uses jsPDF to instantly generate and download formatted A4 application receipts without server overhead.

Strict File Uploads: A multipart/form-data pipeline that actively blocks files over 2MB and strictly filters for .pdf, .jpg, and .png MIME types before sending them to the server via Axios.

👨‍💼 Administrator Module

Real-Time Quota Tracker: An interactive UI that aggregates approved applications and tracks them against a strict allocation quota (10 Boys / 10 Girls / 20 Total).

Secure Document Verification: Admins fetch physical proof documents (AGE_PROOF, PERFORMANCE_CERT, PHOTOGRAPH) via protected endpoints returning binary Blob data, which React renders safely in new browser tabs.

Instant State Management: Approving or rejecting an application updates the PostgreSQL database and instantly refreshes the React UI state without reloading the page.

🛠️ Technology Stack & Libraries

Frontend:

React (Hooks: useState, useEffect, useLocation)

React Router DOM (useNavigate, Link)

Formik & Yup (Form state & Schema validation)

Axios (Promise-based HTTP client)

jsPDF (PDF generation)

Pure CSS (Card-based UI, soft shadows, flexbox layouts)

Backend:

Java 17 / Spring Boot 3.x

Spring Security (JWT, BCryptPasswordEncoder)

Spring Data JPA / Hibernate

PostgreSQL Driver

📂 Key Frontend Structure

src/
├── assets/
│   └── cpcl-logo.webp           # Brand assets
├── components/
│   ├── Login.jsx                # Handles JWT retrieval & role routing
│   ├── Register.jsx             # New user creation & regex validation
│   ├── ApplicationForm.jsx      # Formik/Yup dynamic form & jsPDF receipt
│   ├── DocumentUpload.jsx       # 2MB/MIME strict multipart upload zones
│   └── AdminDashboard.jsx       # Quota tracker & Blob document viewer
└── AppStyles.css                # Global enterprise CSS (Card UI, Flexbox)


🚀 Installation & Setup

1. Database Setup (PostgreSQL)

Create a database named sparc_db.
CRITICAL: To ensure data integrity matches the Formik frontend validation, you must apply this specific Check Constraint to the applications table:

ALTER TABLE applications ADD CONSTRAINT applications_achievement_check CHECK (
    (scholarship_level = 'ELITE_SCHOLAR' AND tournament_level = 'INTERNATIONAL' AND achievement = 'PARTICIPATION') OR
    (scholarship_level = 'ELITE_SCHOLAR' AND tournament_level = 'NATIONAL' AND achievement IN ('TOP_1_5', 'REPRESENTATION')) OR
    (scholarship_level = 'SCHOLAR' AND tournament_level = 'NATIONAL' AND achievement IN ('RANK_6_10', 'QUARTER_FINALIST')) OR
    (scholarship_level = 'SCHOLAR' AND tournament_level = 'STATE' AND achievement IN ('STATE_MEDALIST', 'REPRESENTATION'))
);


2. Backend Setup (Spring Boot)

Update src/main/resources/application.properties:

spring.datasource.url=jdbc:postgresql://localhost:5432/sparc_db
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
spring.jpa.hibernate.ddl-auto=update

# Configure local directory for multipart file uploads
file.upload-dir=C:/uploads/sparc/


Run the application via your IDE or Maven:

mvn spring-boot:run


3. Frontend Setup (React)

Navigate to your frontend directory and run:

# Install required libraries
npm install axios formik yup react-router-dom jspdf

# Start the dev server
npm start
