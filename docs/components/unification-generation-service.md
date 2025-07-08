# Unification Generation Service

The **Unification Generation Service** is the central assembly point of the entire backend pipeline. It is responsible for taking the structured analysis of the AI system and the semantic analysis of the user's questions and unifying them into a coherent whole. It operates in two distinct phases: first generating clarification options for the user, and second, generating the final auditing artifacts based on the user's feedback.

- **Technology:** Python, Flask, RapidFuzz
- **Port:** `5504`

## Key Responsibilities

- **Unification of Inputs:** Merges the `Workflow` object from the `AI System Analyzer` with the analyzed questions from the `PQ Analyzer`.
- **Ambiguity Resolution:** Generates intelligent mapping suggestions by matching terms from the user's questions to specific steps and variables in the AI system workflow.
- **User-Guided Disambiguation:** Creates the data structure (`UnificationClarificationDto`) that powers the interactive clarification screen on the frontend.
- **Trace Template Generation:** Constructs the final JSON trace templates for each step of the AI workflow, enriching them with any additional fields requested by the user.
- **SPARQL Query Generation:** Generates executable SPARQL queries based on the user's original questions and their final, unambiguous mapping choices.
- **Artifact Packaging:** Bundles the final trace templates and SPARQL queries into a single `.zip` archive for the user to download.

---

## The Two-Phase Unification Workflow

### Phase 1: Generating Clarification Suggestions

This phase is the intelligent core of the service, responsible for finding potential matches between the user's informal questions and the formal components of the AI system. The process is a highly structured, concept-driven workflow.

#### A Step-by-Step Walkthrough

Let's trace how the system processes a formalized question like `('who', 'generate', 'data')`, which came from the original question "Who generated the data?".

**1. Concept-Driven Handler Selection**
The process starts with the `target_concept` identified by the `PQ Analyzer`. For our example, this concept is `accountability_wasAttributedTo`. The service uses this unique string as a key to look up the appropriate handler function in its `mapping_functions` registry.

```python
# The service finds a match for "accountability_wasAttributedTo"
# and selects the handle_accoutability_wasAttributedTo function.
mapping_functions = [
    ("accountability_wasAttributedTo", handle_accoutability_wasAttributedTo),
    # ... other handlers
]
```

**2. Activity Matching (`ai_system_activity_finder`)**
The selected handler function is called. It takes the predicate from the formalized question ("generate") and delegates the search to the `find_matching_activity` function. This finder uses a powerful, two-pronged hybrid matching strategy:

- **a) Semantic Type Matching (Primary Strategy):** It first attempts a robust, semantic match. It looks up the keyword "generate" in its `keyword_to_activity_subtype` dictionary and finds that it corresponds to the subtype `"ns#Generation"`. It then scans the entire AI workflow and retrieves all steps that have this specific `stepSubtype`.
- **b) Fuzzy String Matching (Secondary Strategy):** As a supplement, it uses `rapidfuzz.partial_ratio` to compare the keyword "generate" against the `stepName` and `id` of every single step in the workflow. This catches cases where a step might be named "Data Generation" or "Generate Report," even if its type is different.

The results from both strategies are combined into a single list of potentially matching activity IDs.

**3. Contextual Entity Matching (`ai_system_entity_finder`)**
Next, the handler function takes the object from the formalized question ("data") and passes it—along with the list of activity IDs found in the previous step—to the `find_matching_entity_for_activity` function. This contextual link is critical: the service **only searches for entities within the scope of the already-matched activities**.

This finder uses the same hybrid strategy as the activity finder:

- **a) Semantic Type Matching:** It looks up "data" in its `keyword_to_entity_subtype` dictionary, finds it corresponds to `"ns#TrainingDataset"`, and then searches the input/output variables of the matched activities for any variables with this subtype.
- **b) Fuzzy String Matching:** It uses `rapidfuzz` to compare "data" against the names/IDs of all input/output variables within the matched activities.

**4. Assembling the Suggestions**
The final result is a structured list of mapping suggestions. For each activity that was matched, there is a corresponding list of entities that were matched within that activity's context. This data is then formatted into the `UnificationClarificationDto` and sent to the frontend, empowering the user to make the final, definitive choice.

!> **Extensibility Note:** The dictionaries used for semantic matching (e.g., `keyword_to_activity_subtype`) are prime candidates for being extracted into external configuration files (e.g., JSON) to make the service even more configurable without code changes.

### Phase 2: Generating Final Artifacts

This phase is triggered after the user submits their choices from the frontend's clarification screen. The service uses this definitive, user-provided mapping to generate the final outputs.

#### 1. Trace Template Generation (`trace_template_generation.py`)

The service first creates a baseline template for every step in the AI workflow, using the `defaultTraceFields` that were pre-populated by the `AI System Analyzer`. It then enriches this baseline by injecting any additional trace fields the user requested during the clarification step.

#### 2. SPARQL Query Generation (`sparql_query_generation.py`)

Using a set of `string.Template` objects, the service selects the correct SPARQL template based on the question's `target_concept`. It then substitutes placeholders in the template with the specific activity ID the user selected, creating a precise, executable query.

!!> **TODO: Externalize SPARQL Templates**

> A critical future improvement is to extract the SPARQL templates from inline strings into separate `.sparql` configuration files to improve maintainability.

#### 3. Artifact Packaging (`output_util.py`)

This is the final step where the in-memory artifacts are written to disk and packaged.

1.  **Write Trace Files:** The `generate_output_trace_files` function creates an `output/traces/` directory and writes each generated trace template into its own `.json` file.
2.  **Write SPARQL Files:** The `generate_output_sparql_files` function creates an `output/sparql/` directory. It writes each generated query into a `.sparql` file, adding the original user question as a comment (`#`) at the top for context.
3.  **Create ZIP Archive:** The `generate_output_zip_file` function creates an `output.zip` archive and adds the contents of the `traces` and `sparql` directories into it, preserving the folder structure.
4.  **Cleanup:** The temporary `output/` directory is removed, leaving only the final `output.zip` file.

---

## For Developers (Living Documentation)

### Development & Build Setup

#### Dependencies

The service relies on a few key Python libraries, defined in `requirements.txt`:

- `Flask`: The web server framework for the API.
- `rapidfuzz`: For high-performance fuzzy string matching.
- `flask_cors`: To handle Cross-Origin Resource Sharing (CORS) for the frontend.

#### Local Development

To run the service locally, navigate to its root directory and start the Flask server:

```bash
# This will start the Flask server
python main.py
```

#### Docker Build Process

The service is containerized using a `Dockerfile` that creates a clean and efficient production environment.

1.  **Base Image:** It starts from a `python:3.11-slim` image.
2.  **Copy & Install:** It copies the application code and installs the dependencies from `requirements.txt`.
3.  **Environment Variable:** It sets `ENV PYTHONUNBUFFERED=1`, a best practice which ensures that `print` statements are sent directly to the container's logs without being buffered.
4.  **Execution:** The `CMD` instruction runs the Flask application when the container starts.

<details>
<summary><b>Click to see the full Dockerfile</b></summary>

```dockerfile
FROM python:3.11-slim

WORKDIR /app

COPY . /app

RUN pip install --no-cache-dir -r requirements.txt

ENV PYTHONUNBUFFERED=1

CMD ["python", "main.py"]
```

</details>

### State Management via Caching

The service needs to remember the initial analysis results to use them in Phase 2. It does this by using simple, in-memory Python dictionaries as a cache, keyed by the `request_id`.
!> This in-memory caching approach means the service is **stateful**. A restart will clear the cache and cause any in-progress workflows to fail.

### API Endpoints

#### `POST /api/v1/unification-first-step/<request_id>`

- **Role:** Initiates Phase 1.
- **Request:** `multipart/form-data` containing `ai_system_analyzer` (JSON) and `pq_analyzer` (JSON) file parts.
- **Response:** `200 OK` with a JSON object containing mapping suggestions for the frontend.

#### `POST /api/v1/unification-response/<request_id>`

- **Role:** Initiates Phase 2.
- **Request:** A JSON body containing the user's final clarification choices.
- **Response:** `200 OK` with a `mimetype` of `application/zip`. The body contains the raw bytes of the final `output.zip` archive.
