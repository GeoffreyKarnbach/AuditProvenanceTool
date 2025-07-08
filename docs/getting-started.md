# Getting started

Welcome to the **AI Auditing & Provenance Question Tool**!  
This project is designed for who needs to bridge the gap between high-level policy questions and the technical specifics of an AI system. It assists users in querying, interpreting, and understanding the decision-making processes of complex systems through natural language—grounded in provenance concepts and best practices for transparency.

## How It Works in a Nutshell

This tool transforms your natural language questions into formal, machine-readable artifacts through a simple, interactive process:

1.  **You Provide:** You supply two key inputs: a formal description of your AI system's workflow (as a `.ttl` file) and a plain text file with your auditing questions.
2.  **The Tool Analyzes:** In the background, a powerful pipeline of microservices parses your AI system's structure and runs your questions through a multi-stage Natural Language Processing (NLP) engine.
3.  **You Clarify:** The tool's real power is its interactive UI. If a question is ambiguous (e.g., does "process data" refer to "pre-processing" or "post-processing"?), the tool presents you with the options and lets you make the definitive choice.
4.  **You Get:** Based on your clarifications, the tool generates a `.zip` archive containing two concrete sets of artifacts: executable SPARQL queries and JSON trace templates, ready for use in an auditing environment (such as the AuditBox).

## What You'll Need

To get started, you need two files:

- **An AI System Description (`.ttl` file):** A file written in the Turtle RDF syntax that formally describes your AI workflow's steps, data artifacts, and actors, using ontologies like [PROV-O](https://www.w3.org/TR/prov-o/) and [P-Plan](https://www.opmw.org/model/p-plan/).
- **A List of Auditing Questions (`.txt` file):** A plain text file with one natural language question per line (e.g., "Who was involved in training the model?").

## What You'll Get

The final output is a single `output.zip` file containing two folders:

- **/traces/**: This folder contains a set of **JSON Trace Templates**. Each JSON file is a schema that defines the provenance data a specific step in your AI workflow should emit during its execution. These can be used to instrument your system for transparent logging.
- **/sparql/**: This folder contains **Executable SPARQL Queries**. Each `.sparql` file corresponds to one of your original questions and can be run against a provenance database (like an AuditBox) that has collected data matching the trace templates.

## Key Features

- **Semantic Workflow Parsing:** The tool uses Apache Jena to parse complex AI system descriptions based on formal ontologies like PROV-O and P-Plan.
- **Multi-Stage NLP Pipeline:** Questions are not just matched; they are processed through a pipeline that rewrites, formalizes them into Subject-Predicate-Object triplets, and maps them to core provenance concepts.
- **Interactive Ambiguity Resolution:** A core feature of the tool. The Angular-based frontend provides a guided workflow for users to resolve ambiguities, ensuring the final outputs are accurate.
- **Modular Microservice Architecture:** The backend is composed of independent, specialized services, allowing for technological flexibility (Java for semantic web, Python for NLP) and scalability.

## Run it yourself

!> You need Docker and Docker Compose installed locally to run the project.

To run the full tool on your local machine:

- Clone the [GitHub Repo](https://github.com/GeoffreyKarnbach/AuditProvenanceTool)
- From the root directory of the project, build and run the services with:
  ```bash
  docker-compose up -d --build
  ```
- Open your browser and navigate to `http://localhost:5503`
