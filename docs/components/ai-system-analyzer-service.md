# AI System Analyzer Service

The **AI System Analyzer** is the first processing service in the workflow pipeline. Its dedicated role is to parse a semantic description of an AI system, provided as a Turtle (`.ttl`) file, and transform it into a structured, sequential workflow object that has been pre-enriched with default trace fields.

- **Technology:** Java, Apache Jena
- **Port:** `5500`

## Key Responsibilities

- **Semantic Parsing:** Uses the Apache Jena library to read and interpret the RDF triples from the input `.ttl` file.
- **Concept Extraction:** Identifies and extracts key individuals from the RDF graph that represent core provenance concepts: Steps (`pplan:Step`), Variables (`pplan:Variable`), and Agents (`audit:Actor`).
- **Sequential Structuring:** Analyzes the `pplan:isPrecededBy` relationships to organize the extracted steps into a clear, linear sequence.
- **Default Trace Field Enrichment:** Based on the type of each step, variable, and agent, it enriches them with a pre-configured list of default fields for later use in trace template generation.
- **JSON Serialization:** Converts the final, enriched workflow object into a standardized JSON format for the Director service to consume.

---

## Input File Requirements (TTL Structure)

To be successfully parsed, the input `.ttl` file must adhere to a specific structure. The service relies on a combination of `rdf:type` definitions and specific properties to build the workflow.

!> **Critical Assumption: Linear Workflow**

> The parser is designed for workflows that are strictly **sequential and non-branching** (i.e., they behave like a linked list). There must be exactly **one** starting step, and every other step must be reachable from it. Workflows with parallel branches are not currently supported.

### Defining a Step

Each step in the workflow must be an individual with the following characteristics:

- **Must** have `rdf:type pplan:Step`.
- **Must** have a second `rdf:type` from the `audit:` namespace (e.g., `audit:Training`) to identify its subtype.
- **Should** have an `rdfs:label` for a human-readable name.
- **May** have one or more `pplan:hasInputVar` properties pointing to its input `pplan:Variable`(s).
- **May** have one or more `pplan:hasOutputVar` properties pointing to its output `pplan:Variable`(s).
- **Must**, unless it is the first step, have exactly one `pplan:isPrecededBy` property pointing to the previous `pplan:Step`.

### Defining a Variable

Each variable (data, model, or artifact) must be an individual with:

- **Must** have `rdf:type pplan:Variable`.
- **Must** have a second `rdf:type` from the `audit:` namespace (e.g., `audit:TrainingDataset`) for its subtype.
- **Should** have an `rdfs:label`.

### Defining an Agent

Each agent (person or team) must be an individual with:

- **Must** have `rdf:type audit:Actor`.
- **Should** have an `rdfs:label`.
- **Must** be linked to the step(s) it performs using the `audit:operate` property. An agent can operate on multiple steps.

<details>
<summary><b>Click to see a Minimal Working TTL Example</b></summary>

```turtle
@prefix : <https://w3id.org/audit/ns/> .
@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .
@prefix pplan: <http://purl.org/net/p-plan#> .
@prefix audit: <https://w3id.org/audit/ns/> .

# --- Define Classes (Good Practice, but not strictly parsed) ---
audit:Actor rdf:type rdfs:Class .
audit:Training rdf:type rdfs:Class ; rdfs:subClassOf pplan:Step .
audit:Generation rdf:type rdfs:Class ; rdfs:subClassOf pplan:Step .
audit:TrainingDataset rdf:type rdfs:Class ; rdfs:subClassOf pplan:Variable .

# --- Define Individuals (These are parsed by the service) ---

# 1. Agent
audit:MyTeam rdf:type audit:Actor ;
             rdfs:label "My Team" ;
             audit:operate audit:Step1_GetData, audit:Step2_TrainModel .

# 2. Variables
audit:RawData rdf:type pplan:Variable, audit:TrainingDataset ;
              rdfs:label "Raw Input Data" .

audit:TrainedModel rdf:type pplan:Variable, audit:StatisticalModel ;
                   rdfs:label "Final Trained Model" .

# 3. Steps
# This is the FIRST step (no pplan:isPrecededBy)
audit:Step1_GetData rdf:type pplan:Step, audit:Generation ;
                    rdfs:label "Get The Data" ;
                    pplan:hasOutputVar audit:RawData .

# This is the SECOND step
audit:Step2_TrainModel rdf:type pplan:Step, audit:Training ;
                       rdfs:label "Train The Model" ;
                       pplan:hasInputVar audit:RawData ;
                       pplan:hasOutputVar audit:TrainedModel ;
                       pplan:isPrecededBy audit:Step1_GetData .
```

</details>

---

## For Developers (Living Documentation)

This section provides a deep dive into the service's internal logic and configuration.

### Development & Build Setup

#### Local Development

To run the service from your IDE, locate the main application class (annotated with `@SpringBootApplication`) and run its `main` method. In IntelliJ, this is done by clicking the green "Run" arrow next to the class.

#### Docker Build Process

The service is containerized using a multi-stage `Dockerfile` for an optimized production image.

1.  **Build Stage:** A `maven:3.9-eclipse-temurin-17` image is used to build the application. It leverages Docker layer caching for dependencies to speed up subsequent builds.
2.  **Production Stage:** It starts from a minimal `eclipse-temurin:17-jdk-alpine` base image and copies _only_ the final `app.jar` from the build stage, creating a small and secure final image.

<details>
<summary><b>Click to see the full Dockerfile</b></summary>

```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY checkstyle.xml .

COPY src ./src
RUN mvn clean package -DskipTests -Dcheckstyle.skip=true

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

</details>

### Internal Processing Pipeline

The service processes the input TTL file in a two-phase pipeline:

1.  **TTL to Structured Workflow Mapping:** The `TTLMapper` class parses the RDF triples and converts them into an ordered `Workflow` object.
2.  **Default Trace Field Enrichment:** The `WorkflowDefaultTraceAdder` class then iterates over this `Workflow` object and populates the `defaultTraceFields` list within each DTO based on external JSON configuration files.

### Phase 2: Enriching with Default Trace Fields

This phase is **highly configurable**. It adds a list of suggested trace fields to each element by looking up its subtype URI (e.g., `https://w3id.org/audit/ns#Generation`) and a generic `"default"` key in a set of JSON files located in the `resources/traceFields/` directory.

<details>
<summary><b>Click to see the Trace Field Configuration Files</b></summary>

**`resources/traceFields/stepTypes.json`**

```json
{
  "https://w3id.org/audit/ns#Generation": [
    "GenerationStepA",
    "GenerationStepB"
  ],
  "default": [
    "label",
    "id",
    "start_time",
    "end_time",
    "inputs",
    "output",
    "agent"
  ]
}
```

_... (and so on for other types)_

</details>

?> **Note on Type Hierarchies:** The current system does not automatically resolve RDF class hierarchies for trace fields. If a type is a subtype of another, its entry in the JSON configuration should include all fields from its parent types.

### API Endpoint

The service exposes a single REST endpoint.

#### `POST /api/v1/analyze-system`

- **Request:** `multipart/form-data` with a file part named `system_description`.
- **Success Response:** `200 OK` with the JSON representation of the enriched `Workflow` object.
