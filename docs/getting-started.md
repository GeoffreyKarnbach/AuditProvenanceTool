# Getting started

Welcome to the **AI Auditing & Provenance Question Tool**!  
This project aims to assist users in querying, interpreting, and understanding the decision-making processes of AI systems through natural language questions—grounded in provenance concepts and best practices for transparency.

## Overview

This tool helps you:

- **Query AI workflows** using natural language.
- **Map questions to formal provenance models** (e.g., [PROV-O](https://www.w3.org/TR/prov-o/), [P-Plan](https://www.opmw.org/model/)).
- **Audit the execution of AI systems** and trace activities, entities and agents.
- **Visualize and interpret** the answers by running them in the AuditBox.

## Features

- AI System Description parsing using Apache JENA
- Natural Language to Ontology Question Mapping
- NLP-based question rewriting and formalization
- Modular microservice architecture
- Angular-based interactive frontend

## Run it yourself

!> You need docker installed locally, in order to build the project.

To be able to run the tool locally:

- Clone the [GitHub Repo](https://github.com/GeoffreyKarnbach/AuditProvenanceTool)
- Build and run the project by running from the root directory of the project: `docker-compose up -d --build`
- Go to `http://localhost:5503`
