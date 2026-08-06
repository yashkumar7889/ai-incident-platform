# 🚨 Sentinel AI – Intelligent Incident Management Platform

Sentinel AI is a modern Incident Management Platform built with **Spring Boot**, **PostgreSQL**, **Spring AI**, **Ollama**, and **PGVector**.

The platform enables organizations to manage incidents while leveraging Local Large Language Models (LLMs) for intelligent incident analysis, semantic search, severity prediction, root cause analysis, and Retrieval-Augmented Generation (RAG).

---

# ✨ Features

## Incident Management

- Create Incident
- Update Incident
- Delete Incident
- Get Incident by ID
- List Incidents with Pagination
- Filter by Severity
- Filter by Status
- Keyword Search

---

## AI Features

### 💬 AI Chat

Interact with a locally running LLM.

Example:

> Explain what database replication is.

---

### 📝 Incident Summarization

Generate concise summaries for lengthy incident descriptions.

---

### ⚠️ Severity Prediction

Automatically predicts:

- LOW
- MEDIUM
- HIGH
- CRITICAL

along with the reasoning.

---

### 🔍 Root Cause Analysis

Uses AI to determine the most probable root cause from an incident description.

---

### 📚 Semantic Search (RAG)

Finds similar historical incidents using vector embeddings stored in PostgreSQL.

---

### ❓ AI Knowledge Assistant

Answers operational questions using previous incidents.

Example:

> Why are users unable to log in?

Instead of searching keywords, the system retrieves semantically similar incidents and provides an AI-generated answer.

---

## Vector Synchronization

Embeddings are automatically maintained throughout the incident lifecycle.

- Create Incident → Create Embedding
- Update Incident → Update Embedding
- Delete Incident → Delete Embedding

This ensures semantic search always reflects the latest incident data.

---

# 🏗 Architecture

```
                   +----------------------+
                   |      REST APIs       |
                   +----------+-----------+
                              |
                     Spring Boot 3
                              |
          +-------------------+-------------------+
          |                                       |
          |                                       |
     PostgreSQL                            Spring AI
          |                                       |
      Incident Data                       Ollama (LLM)
          |                                       |
          +-------------------+-------------------+
                              |
                         PGVector Store
                              |
                      Semantic Search (RAG)
```

---

# 🛠 Technology Stack

| Category | Technology |
|----------|------------|
| Language | Java 21 |
| Framework | Spring Boot 3 |
| ORM | Spring Data JPA |
| Database | PostgreSQL 16 |
| Vector Database | PGVector |
| AI Framework | Spring AI |
| Local LLM | Ollama |
| Embedding Model | nomic-embed-text |
| Chat Model | qwen2.5:0.5b |
| Validation | Jakarta Validation |
| Mapping | MapStruct |
| Testing | JUnit 5 + Mockito |
| Build Tool | Maven |
| Containerization | Docker + Docker Compose |

---

# 📂 Project Structure

```
src
 ├── controller
 ├── service
 ├── repository
 ├── entity
 ├── dto
 ├── mapper
 ├── exception
 ├── specification
 ├── config
 ├── common
 └── ai
```

---

# 🚀 Running the Application

## 1. Clone Repository

```bash
git clone https://github.com/<username>/sentinel-ai.git

cd sentinel-ai
```

---

## 2. Start PostgreSQL

```bash
docker compose up postgres -d
```

---

## 3. Install Ollama

https://ollama.com/download

---

## 4. Pull Models

Chat model

```bash
ollama pull qwen2.5:0.5b
```

Embedding model

```bash
ollama pull nomic-embed-text
```

---

## 5. Run Application

```bash
mvn spring-boot:run
```

---

Swagger

```
http://localhost:8081/swagger-ui.html
```

---

# 🧠 AI Endpoints

## Chat

```
POST /api/v1/ai/chat
```

---

## Summarize Incident

```
POST /api/v1/ai/summarize
```

---

## Predict Severity

```
POST /api/v1/ai/severity
```

---

## Root Cause Analysis

```
POST /api/v1/ai/root-cause
```

---

## Ask Questions using RAG

```
POST /api/v1/ai/ask
```

---

# 📊 REST APIs

## Incident APIs

| Method | Endpoint |
|---------|----------|
| POST | /api/v1/incidents |
| GET | /api/v1/incidents |
| GET | /api/v1/incidents/{id} |
| PUT | /api/v1/incidents/{id} |
| DELETE | /api/v1/incidents/{id} |

---

# 🔍 Semantic Search Workflow

```
Create Incident
        │
        ▼
Generate Embedding
        │
        ▼
Store Vector in PGVector
        │
        ▼
User asks a question
        │
        ▼
Similarity Search
        │
        ▼
Relevant Incidents Retrieved
        │
        ▼
LLM Generates Final Answer
```

---

# 🧪 Testing

Run all tests

```bash
mvn test
```

Build project

```bash
mvn clean package
```

---

# 🐳 Docker

Build

```bash
docker compose build
```

Run

```bash
docker compose up
```

---

# 📸 Screenshots

Suggested screenshots to include:

- Swagger UI
- AI Chat
- Severity Prediction
- Root Cause Analysis
- RAG Question Answering
- PostgreSQL Tables
- PGVector Table
- Docker Containers

---

# 🚀 Future Enhancements

- Authentication & Authorization (JWT)
- Role-Based Access Control
- Streaming AI Responses
- Similar Incident Recommendations
- AI-generated Postmortem Reports
- Incident Timeline Visualization
- Prometheus & Grafana Monitoring
- Kubernetes Deployment
- CI/CD with GitHub Actions

---

# 👨‍💻 Author

**Your Name**

Java Backend Developer

- GitHub: https://github.com/<username>
- LinkedIn: https://linkedin.com/in/<username>

---

# ⭐ Acknowledgements

Built using:

- Spring Boot
- Spring AI
- Ollama
- PostgreSQL
- PGVector
- MapStruct
- Docker

---

## ⭐ If you found this project useful, consider giving it a star!