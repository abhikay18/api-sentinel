# API Sentinel 🛡️

**Intelligent API Governance Platform**

A backend-focused API governance and security platform built with Spring Boot that enforces rate limiting, abuse detection, and AI-assisted anomaly analysis for modern API ecosystems.

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Latest-blue.svg)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-Latest-red.svg)](https://redis.io/)

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Key Features](#-key-features)
- [Architecture](#-architecture)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [AI / ML Module](#-ai--ml-module)
- [Screenshots](#-screenshots)
- [Getting Started](#-getting-started)
- [Design Philosophy](#-design-philosophy)
- [Future Enhancements](#-future-enhancements)
- [Author](#-author)

---

## 🎯 Overview

API Sentinel protects your APIs from abuse and abnormal traffic patterns through a multi-layered approach combining deterministic backend logic with AI-assisted risk analysis. All enforcement decisions remain **backend-driven and explainable**, with AI serving as a supporting subsystem rather than core logic.

> **Perfect for:** Production API platforms requiring robust governance, security teams needing visibility into API abuse patterns, and developers building scalable API infrastructure.

---

## ✨ Key Features

### 🔐 API Key Management
- Secure API key validation and activation
- Plan-based rate limit enforcement
- Usage tracking and analytics
- Admin revocation support

### ⚡ Rate Limiting Engine
- **Redis-backed sliding window algorithm**
- Per-plan request thresholds
- Automatic temporary blocking on violations
- Cooldown-based recovery mechanism

### 🚨 Abuse Detection System
- Centralized blocking logic
- Hybrid Redis + Database enforcement
- Distinguishes between **SYSTEM** and **AI-triggered** blocks
- Prevents stale or duplicate blocks

### 🧠 AI-Assisted Anomaly Detection
- Per-API-key baseline learning
- Adaptive thresholds based on traffic maturity
- **Explainable AI decisions** with human-readable reasons
- No blocking during warm-up phase
- Fully overrideable by administrators

### 📊 Baseline Learning System
- Rolling averages for traffic patterns
- Request rate and endpoint diversity analysis
- Sample-based maturity gate
- Continuous learning and adaptation

### 📈 Admin Dashboard
- Real-time blocked API monitoring
- AI confidence levels and risk scores
- Detailed AI explanations per block
- Historical AI score trend visualization
- Manual unblock controls
- Baseline reset and retraining tools

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────┐
│                   Client                        │
└──────────────────┬──────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────┐
│            API Gateway Filter                   │
└──────────────────┬──────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────┐
│         API Key Authentication                  │
└──────────────────┬──────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────┐
│        Rate Limiting (Redis)                    │
└──────────────────┬──────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────┐
│      AI Risk Evaluation (Explainable)           │
└──────────────────┬──────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────┐
│       Abuse Detection & Blocking                │
└──────────────────┬──────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────┐
│   Admin Dashboard (Monitoring & Control)        │
└─────────────────────────────────────────────────┘
```

---

## 🛠️ Tech Stack

### Backend
- **Java 21** - Modern Java features and performance
- **Spring Boot** - Application framework
- **Spring MVC** - Web layer
- **Spring Data JPA** - Data persistence
- **Hibernate** - ORM
- **Spring Security** - Filter-based security

### Data & Caching
- **PostgreSQL** - Primary database
- **Redis** - Caching and rate limiting

### AI & Analytics
- **Isolation Forest** - Anomaly detection algorithm
- Baseline deviation analysis
- Explainable AI scoring engine
- Adaptive threshold algorithms
- Time-series risk tracking

### Frontend (Admin Panel)
- **Thymeleaf** - Server-side templating
- **HTML/CSS** - UI styling
- **Chart.js** - Data visualization

### DevOps
- Docker-ready architecture
- Stateless backend design
- Cloud-deployable infrastructure

---

## 📁 Project Structure

```
api-sentinel/
├── security/              # API filters & enforcement
├── rate_limit/            # Rate limiting engine
├── abuse/                 # Blocking & cooldown logic
├── ai/                    # AI analysis & baselines
├── controller/admin/      # Admin controllers
├── domain/                # JPA entities
├── repository/            # Data access layer
├── templates/             # Thymeleaf UI templates
├── ApiGovernanceApplication.java
└── ai-ml-service/         # Standalone ML module
    ├── app.py             # FastAPI application
    ├── model.py           # Isolation Forest model
    └── requirements.txt   # Python dependencies
```

---

## 🤖 AI / ML Module

The machine learning logic is **intentionally isolated** in a separate module (`ai-ml-service/`) to ensure:

- ✅ **Backend-first system design** - Spring Boot handles all core logic
- ✅ **Loose coupling** between AI and enforcement systems
- ✅ **Independent ML experimentation** and deployment
- ✅ **Technology flexibility** - Use Python's rich ML ecosystem

**The backend can operate fully without the ML module.**

### ML Architecture

The AI service uses **Isolation Forest**, an unsupervised anomaly detection algorithm that:
- Identifies outliers in multi-dimensional feature space
- Requires no labeled training data
- Provides anomaly scores for risk assessment
- Efficiently handles high-dimensional data

**Features analyzed:**
- `requestsPerMinute` - Traffic volume
- `errorCount` - Error rate
- `uniqueEndpoints` - Endpoint diversity
- `avgRequestIntervalMs` - Request timing patterns

The model returns both a binary anomaly flag and a continuous risk score for explainability.

---

## 📸 Screenshots

### Dashboard Overview
![Dashboard](screenshots/dashboard.png)

### Abuse Monitor with AI Explanation
![Abuse Monitor](screenshots/abuse-monitor.png)

### AI Risk Score Trend
![AI Trend](screenshots/ai-trend.png)

### Baseline Controls
![Baseline Controls](screenshots/baseline-controls.png)

---

## 🚀 Getting Started

### Prerequisites
- Java 21 or higher
- PostgreSQL 12+
- Redis 6+
- Maven 3.8+
- Docker (optional, for Redis)
- Python 3.8+ (for AI/ML module)
- pip (Python package manager)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/abhikay18/api-sentinel.git
   cd api-sentinel
   ```

2. **Start Redis using Docker**
   ```bash
   docker run -d -p 6379:6379 --name redis redis
   ```
   
   To verify Redis is running:
   ```bash
   docker ps | grep redis
   ```

3. **Configure application properties**
   ```bash
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   # Edit application.properties with your database and Redis credentials
   ```

4. **Build the project**
   ```bash
   mvn clean install
   ```

5. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

6. **Access the admin dashboard**
   ```
   http://localhost:8080/admin
   ```

### Running the AI/ML Service (Optional)

The AI service is optional but recommended for enhanced anomaly detection.

1. **Navigate to the AI service directory**
   ```bash
   cd ai-ml-service
   ```

2. **Install Python dependencies**
   ```bash
   pip install -r requirements.txt
   ```

3. **Start the FastAPI service**
   ```bash
   uvicorn app:app --port 8000
   ```

4. **Verify the AI service is running**
   ```bash
   curl http://localhost:8000/health
   ```
   
   Expected response: `{"status": "UP"}`

The Spring Boot application will automatically connect to the AI service if it's running on port 8000.

### Testing

Run the test suite:
```bash
mvn test
```

Trigger AI anomaly detection (testing endpoint):
```bash
curl -X POST http://localhost:8080/test/trigger-anomaly?apiKey=YOUR_API_KEY
```

---

## 💡 Design Philosophy

API Sentinel follows production-grade principles:

- **Backend logic is authoritative** - All enforcement decisions are deterministic
- **AI provides signals, not decisions** - AI assists but doesn't control
- **Every AI action is:**
  - 📝 Logged for audit trails
  - 📖 Explainable with human-readable reasons
  - 🔄 Overrideable by administrators
- **System remains functional without AI** - Core features work independently

This mirrors real-world production API security systems used by major platforms.

---

## 🚧 Future Enhancements

- [ ] OAuth2 / JWT authentication support
- [ ] Prometheus & Grafana monitoring integration
- [ ] Distributed tracing with OpenTelemetry
- [ ] Alerting integration (Slack, PagerDuty)
- [ ] Multi-tenant support
- [ ] GraphQL API support
- [ ] Machine learning model improvements
- [ ] API documentation with Swagger/OpenAPI

---

## 📌 Resume Highlights

- Built a **production-grade API governance platform** with Spring Boot
- Implemented **Redis-backed rate limiting** and abuse prevention mechanisms
- Designed **AI-assisted anomaly detection** with full explainability
- Created comprehensive **admin tooling** for real-time monitoring and control
- Followed **clean architecture** principles and separation of concerns
- Demonstrated expertise in **backend security** and **distributed systems**

---

## 👤 Author

**Abhishek Kumar**  
Backend & Software Engineer

[![GitHub](https://img.shields.io/badge/GitHub-abhikay18-black?style=flat&logo=github)](https://github.com/abhikay18)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-blue?style=flat&logo=linkedin)](https://linkedin.com/in/YOUR_LINKEDIN)

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome! Feel free to check the [issues page](https://github.com/abhikay18/api-sentinel/issues).

---

## ⭐ Show Your Support

Give a ⭐️ if this project helped you!

---

<div align="center">
  <sub>Built with ❤️ by Abhishek Kumar</sub>
</div>
