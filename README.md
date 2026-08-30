# Incident Investigator

> AI-powered platform for intelligent payment incident investigation, combining **Spring Boot, Generative AI, RAG, vector search and automated investigation workflows**.

## 📌 Overview

**Incident Investigator** is an intelligent application designed to assist technical teams in analyzing and investigating payment transaction incidents.

The platform combines traditional software engineering with **Generative AI, Retrieval-Augmented Generation (RAG), and agentic AI** to provide contextualized investigation results.

The system analyzes incident information, transaction data, client contracts, technical documentation and historical investigations to assist users in identifying possible causes and generating investigation reports.

---

## 🎯 Objectives

The main objectives of the project are to:

* Centralize payment incident investigation.
* Reduce the time required to analyze incidents.
* Correlate incidents with transaction and contract information.
* Retrieve relevant technical and business knowledge using RAG.
* Use Generative AI to assist with incident diagnosis.
* Provide structured investigation results.
* Maintain an investigation history.
* Generate investigation reports in PDF format.

---

## ✨ Main Features

### 🔎 Incident Investigation

* Incident creation and management.
* Incident status tracking.
* Investigation workflow.
* Correlation with payment transactions.

### 💳 Transaction Analysis

* Transaction search and visualization.
* Transaction status analysis.
* Identification of suspicious or failed transactions.
* Correlation between incidents and transactions.

### 📄 Contract Analysis

* Client contract management.
* Contract information retrieval.
* Contract-aware investigation.

### 🤖 Agentic AI

The platform uses several specialized AI components to support the investigation process:

* Incident orchestration.
* Technical analysis.
* Contract analysis.
* Specification analysis.
* Transaction analysis.
* AI-generated investigation results.

The investigation workflow can be represented as:

```text
User
 │
 ▼
Incident
 │
 ▼
Investigation Orchestrator
 │
 ├───────────────┐
 ▼               ▼
Technical Agent  Contract Agent
 │               │
 └───────┬───────┘
         ▼
   Investigation
      Result
         │
         ▼
    AI-generated
       Report
```

---

## 🧠 Retrieval-Augmented Generation (RAG)

The application integrates a **RAG architecture** to provide the AI with relevant domain knowledge before generating an answer.

The knowledge base contains technical and business documentation such as:

* Payment message structures.
* Response codes.
* Diagnostic procedures.
* Timeout scenarios.
* Rejection scenarios.
* Reconciliation issues.
* Client contract information.

General RAG workflow:

```text
Documents
    │
    ▼
Document Ingestion
    │
    ▼
Chunking / Embeddings
    │
    ▼
Vector Store
    │
    ▼
Similarity Search
    │
    ▼
Relevant Context
    │
    ▼
Google Gemini
    │
    ▼
Investigation Result
```

---

## 🏗️ Architecture

The project follows a **modular, feature-oriented Spring Boot architecture**.

```text
src/main/java/
└── com/hps/pfa/incident_investigator/
    │
    ├── agent/
    │   └── AI agents and investigation orchestration
    │
    ├── incident/
    │   └── Incident management
    │
    ├── transaction/
    │   └── Transaction management and analysis
    │
    ├── contract/
    │   └── Client contract management
    │
    ├── history/
    │   └── Investigation history
    │
    ├── rag/
    │   └── RAG ingestion and retrieval
    │
    ├── pdf/
    │   └── PDF report generation
    │
    ├── web/
    │   └── Web interface and controllers
    │
    ├── datagen/
    │   └── Test data generation
    │
    └── common/
        └── Shared components and exceptions
```

---

## 🛠️ Technologies

### Backend

* **Java 21**
* **Spring Boot**
* **Spring MVC**
* **Spring Data JPA**
* **Spring AI**
* **Maven**

### Artificial Intelligence

* **Google Gemini**
* **Generative AI**
* **Agentic AI**
* **Retrieval-Augmented Generation (RAG)**
* **Vector embeddings**

### Data

* **PostgreSQL**
* **pgvector**
* **Flyway**

### Frontend

* **Thymeleaf**
* **HTML5**
* **CSS3**
* **JavaScript**

### Other

* PDF report generation
* Maven Wrapper
* JUnit / Spring Boot Test
* Git / GitHub

---

## 📂 Project Structure

```text
incident-investigator/
│
├── .github/
│   └── workflows/
│
├── docs/
│   ├── architecture/
│   ├── deployment/
│   └── screenshots/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/hps/pfa/incident_investigator/
│   │   │
│   │   └── resources/
│   │       ├── db/
│   │       │   └── migration/
│   │       ├── rag/
│   │       │   ├── contracts/
│   │       │   └── knowledge/
│   │       └── templates/
│   │
│   └── test/
│
├── .env.example
├── .gitignore
├── LICENSE
├── README.md
├── pom.xml
├── mvnw
└── mvnw.cmd
```

---

## ⚙️ Requirements

Before running the project, make sure the following are installed:

* Java 21 or later
* Maven 3.9+ (optional, Maven Wrapper is included)
* PostgreSQL
* pgvector extension
* Google Gemini API key

---

## 🔧 Configuration

The application uses environment variables for sensitive configuration.

Create the required environment variables:

```env
DB_URL=jdbc:postgresql://localhost:5432/incident_investigator
DB_USERNAME=pfa_user
DB_PASSWORD=your_password
GOOGLE_AI_KEY=your_google_ai_key
```

> **Important:** Never commit real API keys, passwords or production credentials to GitHub.

---

## 🗄️ Database

The project uses **PostgreSQL** as its relational database.

Database schema changes are managed using **Flyway migrations**:

```text
src/main/resources/db/migration/
```

Example:

```text
V1__init_schema.sql
V2__investigation_history.sql
```

The `pgvector` extension is used to support vector-based retrieval for the RAG system.

---

## ▶️ Running the Application

### Using Maven Wrapper

Linux / macOS:

```bash
./mvnw spring-boot:run
```

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

### Using Maven

```bash
mvn spring-boot:run
```

---

## 🧪 Running Tests

Linux / macOS:

```bash
./mvnw test
```

Windows:

```powershell
.\mvnw.cmd test
```

---

## 📊 Investigation Workflow

A typical investigation follows these steps:

```text
1. Incident creation
        │
        ▼
2. Incident information collection
        │
        ▼
3. Transaction analysis
        │
        ▼
4. Contract verification
        │
        ▼
5. RAG knowledge retrieval
        │
        ▼
6. AI-assisted analysis
        │
        ▼
7. Investigation result
        │
        ▼
8. Report generation
        │
        ▼
9. Investigation history
```

---

## 🔐 Security

The project is designed to keep sensitive configuration outside the source code.

Sensitive values should be provided through environment variables:

```text
GOOGLE_AI_KEY
DB_USERNAME
DB_PASSWORD
DB_URL
```

Do not commit:

```text
.env
API keys
Database passwords
Production credentials
Private client information
```

---

## 🚀 Future Improvements

Potential future improvements include:

* Advanced multi-agent orchestration.
* Improved RAG evaluation.
* Automated incident classification.
* Anomaly detection using Machine Learning.
* Real-time incident monitoring.
* Automated notification workflows.
* Integration with enterprise monitoring systems.
* Advanced investigation analytics.
* Docker-based deployment.
* CI/CD pipeline.
* Observability and monitoring.

---

## 📚 Documentation

Additional project documentation is available in:

```text
docs/
```

This section can contain:

* Architecture documentation.
* RAG architecture.
* Agentic AI workflow.
* API documentation.
* Deployment documentation.
* UML diagrams.
* Screenshots.

---

## 👥 Project

**Incident Investigator**
Java / Spring Boot – AI / RAG Project

Developed as an engineering project focused on combining:

**Software Engineering + Data + Artificial Intelligence + Generative AI**

---

## 📄 License

This project is distributed under the license specified in the `LICENSE` file.
