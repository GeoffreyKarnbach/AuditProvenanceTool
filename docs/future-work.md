# Future Work Ideas

This document outlines potential improvements, new features, and architectural enhancements for the **AI Auditing & Provenance Question Tool**. These ideas are intended to serve as a roadmap for future development and to capture the project's long-term vision.

## Core Architectural Enhancements

These are high-level changes that would improve the robustness, scalability, and flexibility of the entire system.

#### 1. Support for Non-Linear Workflows

- **Current State:** The `AI System Analyzer` assumes a strictly linear workflow where each step has at most one predecessor and one successor (like a linked list).
- **Proposed Change:** Evolve the parsing logic to handle complex, real-world Directed Acyclic Graphs (DAGs). This includes supporting steps with **multiple input branches** (merges) and steps that **trigger multiple parallel branches** (forks).
- **Impact:** This would remove the `stepNumber` concept and allow the tool to model much more sophisticated AI pipelines, such as those with parallel data processing or ensemble model training.

#### 2. Externalized State Management

- **Current State:** Both the `Director Managing Service` and the `Unification Generation Service` use in-memory dictionaries as a cache to manage the state of a user's request. This makes them stateful.
- **Proposed Change:** Replace the in-memory caches with an external, shared state store like **Redis** or a database.
- **Impact:** This would make the services stateless, significantly improving **robustness** (state would survive a service restart) and enabling **horizontal scalability** (multiple instances of a service could be run behind a load balancer).

#### 3. Modular Ontology Support (Long-Term Vision)

- **Current State:** The system's logic, particularly in the `PQ Analyzer` and `Unification Service`, is tightly coupled to the concepts of PROV-O and our custom `audit:` ontology.
- **Proposed Change:** Refactor the services to support different, pluggable ontology "modules." A user could specify that they want to ask questions based on a different standard, such as one for Data Management Plans (DMPs). The system would then load the appropriate configuration for that domain.
- **Impact:** This would transform the tool from a PROV-O-specific solution into a generic, domain-agnostic provenance questioning framework. A potential file structure for a module could be:

  ```
  ontologies/
  └── PROV/
      ├── mapper.py               # Module-specific mapping logic
      ├── concepts.json           # Definitions of concepts for this ontology
      └── mappings/
          ├── prov_agents.json    # Keyword mappings for agents
          └── prov_entities.json  # Keyword mappings for entities
  ```

---

## Service-Specific Improvements

These are more focused enhancements for individual microservices.

#### AI System Analyzer

- **Automatic Subclass Parsing for TTL:**
  - **Current State:** If `audit:StatisticalModel` is a subclass of `audit:Model`, the developer must manually repeat the trace fields for `Model` in the `StatisticalModel` configuration.
  - **Proposed Change:** Enhance the `WorkflowDefaultTraceAdder` to traverse the `rdfs:subClassOf` hierarchy in the TTL file. It would automatically inherit the `defaultTraceFields` from parent classes, simplifying configuration and reducing redundancy.

#### PQ Analyzer & Unification Service

- **Externalize Knowledge Bases & Dictionaries:**

  - **Current State:** The core logic of these services relies on Python dictionaries and lists defined directly in the code (e.g., `concepts`, `associations`, `keyword_to_activity_subtype`).
  - **Proposed Change:** Move this data into external, human-readable configuration files (e.g., `JSON` or `YAML`).
  - **Impact:** This would allow non-developers to extend the tool's vocabulary and mapping rules without touching the Python code, greatly improving maintainability and accessibility.

- **Advanced NLP for Semantic Matching:**
  - **Current State:** Matching between question terms and workflow components relies on keyword lists and fuzzy string matching.
  - **Proposed Change:** Integrate more advanced NLP techniques like **word embeddings** (e.g., Word2Vec, GloVe) or **sentence transformers**. This would allow the system to understand that "create" is semantically similar to "generate" or that "data records" is similar to "training dataset," even if the keywords don't match.
  - **Impact:** This would make the matching process much more robust and less reliant on a perfect keyword dictionary.

#### Unification Generation Service

- **Externalize SPARQL Query Templates:**
  - **Current State:** The SPARQL queries are defined as `string.Template` objects directly within the Python code.
  - **Proposed Change:** Move each query template into its own `.sparql` file within a configuration directory. The service would then load the appropriate file and perform the substitution.
  - **Impact:** This dramatically improves the readability and maintainability of the SPARQL queries. A SPARQL developer could edit them without needing to navigate the Python code.
