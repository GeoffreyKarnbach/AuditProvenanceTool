# Architecture

The **AI Auditing & Provenance Question Tool** is built as a modular, distributed system following a **microservice architecture**. This design separates the system's complex responsibilities—understanding an AI system's structure, interpreting natural language, and unifying these two streams of information—into independent, focused services.

The entire system is orchestrated by the **Director Managing Service**, which manages a sophisticated, asynchronous workflow that ensures a responsive user experience.

## End-to-End Workflow Narrative

The architecture is designed to execute a two-phase workflow: a **parallel analysis and clarification phase**, followed by a **final artifact generation phase**.

![System Workflow](_media/workflow.png)

1.  **Initiation (Frontend → Director):**
    The user uploads their AI System Description (`.ttl`) and Auditing Questions (`.txt`) via the **Frontend**. The Frontend sends these files to the **Director Managing Service**, which immediately generates and returns a unique `processId`. This ID is crucial for the asynchronous polling pattern that follows.

2.  **Parallel Analysis (Director → Analysis Services):**
    The Director kicks off two analysis tasks in parallel to save time:

    - It sends the `.ttl` file to the **AI System Analyzer**, which parses the file into a structured, sequential `Workflow` object and enriches it with default trace fields.
    - It sends the `.txt` file to the **PQ Analyzer**, which runs each question through a 5-stage NLP pipeline to formalize it into a structured triplet and map it to a PROV-O concept.

3.  **First Unification & Clarification (Director → Unification → Frontend):**
    Once the Director confirms (via its internal state) that both analysis services have completed their tasks, it triggers the **Unification Generation Service** for Phase 1. This service uses a hybrid semantic and fuzzy-matching strategy to find potential links between the analyzed questions and the AI workflow components. It returns a set of clarification options to the Director, which are then passed to the **Frontend** to be presented to the user.

4.  **Final Generation (Frontend → Director → Unification → Frontend):**
    After the user resolves all ambiguities on the clarification screen, the **Frontend** sends the final, definitive user choices back to the **Director**. The Director triggers the **Unification Generation Service** for Phase 2. This service now uses the unambiguous mappings to generate the final SPARQL queries and JSON trace templates, packages them into a `.zip` archive, and returns it to the Director. The Director then serves this file to the **Frontend**, which initiates the download for the user.

## Service Overview

The system consists of the following services, each with a highly specialized role:

### [Frontend](components/frontend.md)

- **Tech:** Angular + Bootstrap
- **Role:** Provides the interactive user experience, guiding the user through file upload, the crucial **interactive clarification** of ambiguous mappings, and the final download of the generated artifacts.
- **Port:** `5503`

### [AI System Analyzer](components/ai-system-analyzer-service.md)

- **Tech:** Java + Apache Jena
- **Role:** Parses the formal AI system description (`.ttl`) into a **sequential workflow object**. It then enriches this object by populating each step, agent, and variable with a list of **default trace fields** based on configurable rules.
- **Port:** `5500`

### [PQ Analyzer](components/pq-analyzer-service.md)

- **Tech:** Python + spaCy
- **Role:** Executes a **multi-stage NLP pipeline** on the user's questions. It simplifies, formalizes them into Subject-Predicate-Object (SPO) triplets, and maps them to specific **PROV-O concepts** using a configurable, rules-based knowledge base.
- **Port:** `5501`

### [Unification Generation Service](components/unification-generation-service.md)

- **Tech:** Python + RapidFuzz
- **Role:** Operates in a **two-phase process**. First, it generates clarification options using a hybrid matching strategy. Second, based on user feedback, it generates the final **SPARQL queries** and **JSON trace templates**.
- **Port:** `5504`

### [Director Managing Service](components-director-managing-service.md)

- **Tech:** Java (Spring Boot)
- **Role:** Acts as the central **asynchronous orchestrator** and API gateway. It manages the entire workflow state using a `processId`, communicates with all backend services, and implements the **polling pattern** that the frontend uses to track progress.
- **Port:** `5502`

---

## Deployment with Docker Compose

All services are containerized and orchestrated via Docker Compose. The `depends_on` configuration ensures that the Director and Frontend start only after their dependencies are available.

```yaml
services:
  unification-generation-service:
    build: ./unification-generation-service
    ports:
      - "5504:5504"

  ai-system-analyzer-service:
    build: ./ai-system-analyzer-service
    ports:
      - "5500:5500"

  pq-analyzer-service:
    build: ./pq-analyzer-service
    ports:
      - "5501:5501"

  director-managing-service:
    build: ./director-managing-service
    ports:
      - "5502:5502"
    depends_on:
      - ai-system-analyzer-service
      - pq-analyzer-service
      - unification-generation-service
    environment:
      - mode=prod

  frontend:
    build: ./frontend
    ports:
      - "5503:80"
    depends_on:
      - director-managing-service
    environment:
      - mode=prod
```

---

## Distributed Microservice Benefits

This architecture was chosen to leverage several key advantages:

- **Scalability**: Each component can be scaled independently. If NLP processing becomes a bottleneck, only the `pq-analyzer-service` needs more resources.
- **Separation of Concerns**: Each service is highly focused. The NLP experts can work on the PQ Analyzer without needing to understand the intricacies of Apache Jena, and vice-versa.
- **Technology Flexibility**: Each service uses the best-suited language for its task (e.g., Python for its rich NLP ecosystem, Java for its robust handling of transactions and type safety in the orchestrator).
- **Robustness**: The asynchronous, distributed nature means that a failure in one analysis service does not crash the entire system. The Director can handle such failures gracefully.
