# AI Auditing & Provenance Question Tool 🔎

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com/GeoffreyKarnbach/AuditProvenanceTool)
[![License](https://img.shields.io/badge/License-Proprietary-red)](LICENSE)
[![Docs](https://img.shields.io/badge/docs-live-blue)](https://geoffreykarnbach.github.io/AuditProvenanceTool/#/)

This project provides a powerful, interactive tool to **transform high-level, natural language auditing questions into formal, actionable artifacts** like SPARQL queries and JSON trace templates. It's designed to bridge the gap between human-readable compliance policies and machine-readable system logs.

## The Problem

Auditing complex AI systems is challenging. Questions like "Who was involved in processing the patient data?" are simple for humans but difficult to answer from raw logs. This tool formalizes such questions, making AI systems more transparent and accountable.

## How It Works

The tool guides the user through a simple yet powerful workflow, orchestrated by a robust microservice architecture.

![System Workflow](https://raw.githubusercontent.com/GeoffreyKarnbach/AuditProvenanceTool/main/docs/_media/workflow.png)

1.  **✍️ Provide Inputs:** You upload two files: a formal description of your AI system (`.ttl` file) and a list of plain-text auditing questions (`.txt` file).
2.  **🧠 Automated Analysis:** The system's backend services work in parallel. An NLP pipeline analyzes your questions, while a semantic analyzer parses your system's structure.
3.  **🤝 Interactive Clarification:** This is the core of the tool. If a question is ambiguous (e.g., does "process data" mean _pre-processing_ or _post-processing_?), the UI presents you with the options from your system description, and you make the final choice.
4.  **🎁 Receive Artifacts:** Based on your clarifications, the tool generates a `.zip` archive containing executable SPARQL queries and JSON trace templates, ready for use in your auditing environment.

## ✨ Key Features

- **Semantic Workflow Parsing:** Uses Apache Jena to understand complex AI system descriptions based on formal ontologies like PROV-O and P-Plan.
- **Multi-Stage NLP Pipeline:** Questions are rewritten, formalized into Subject-Predicate-Object triplets, and mapped to core provenance concepts.
- **Interactive Ambiguity Resolution:** An elegant Angular UI ensures that the final outputs are accurate and reflect the user's true intent.
- **Polyglot Microservice Architecture:** Leverages the strengths of multiple technologies: **Java** for robust orchestration and semantic web processing, **Python** for its rich NLP ecosystem, and **Angular** for a modern user interface.

## 🚀 Getting Started

Get the entire stack running on your local machine with just a few commands.

> ⚠️ **Prerequisite:** You must have Docker and Docker Compose installed.

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/GeoffreyKarnbach/AuditProvenanceTool.git
    cd AuditProvenanceTool
    ```
2.  **Build and run the services:**
    ```bash
    docker-compose up -d --build
    ```
3.  **Access the application:**
    Open your browser and navigate to `http://localhost:5503`.

## 📚 Documentation

For a deep dive into the project's architecture, API reference, component-specific details, and future work ideas, **[check out our full documentation site](https://geoffreykarnbach.github.io/AuditProvenanceTool/#/)**.

The documentation includes detailed explanations of:

- [Overall Architecture](https://geoffreykarnbach.github.io/AuditProvenanceTool/#/architecture)
- [Frontend Service](https://geoffreykarnbach.github.io/AuditProvenanceTool/#/components/frontend)
- [Director Managing Service](https://geoffreykarnbach.github.io/AuditProvenanceTool/#/components/director-managing-service)
- [AI System Analyzer](https://geoffreykarnbach.github.io/AuditProvenanceTool/#/components/ai-system-analyzer-service)
- [PQ Analyzer](https://geoffreykarnbach.github.io/AuditProvenanceTool/#/components/pq-analyzer-service)
- [Unification Generation Service](https://geoffreykarnbach.github.io/AuditProvenanceTool/#/components/unification-generation-service)

## 🤝 Contributing

Under the current license, direct contributions such as pull requests cannot be accepted. However, feedback is highly valued. Please feel free to open an issue to report a bug or suggest a new feature.

- A great place to start is by looking at our **[Future Work Ideas](https://geoffreykarnbach.github.io/AuditProvenanceTool/#/future-work)**.

## 📜 License

This project is proprietary and is available under a restrictive, source-available license.

**Permission is granted to view the source code for educational and reference purposes only.**

Any other use, including copying, modifying, distributing, or using the software in a commercial or non-commercial setting, is **strictly prohibited** and requires the explicit, prior **written permission** of the author.

Please see the [LICENSE](LICENSE) file for the full terms.
