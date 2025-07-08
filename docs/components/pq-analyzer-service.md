# PQ Analyzer Service

The **Provenance Question (PQ) Analyzer** is a highly specialized Natural Language Processing (NLP) service. It is one of the most essential components of the tool, responsible for bridging the gap between ambiguous human questions and the formal, structured world of provenance ontologies.

- **Technology:** Python, Flask, spaCy
- **Port:** `5501`

## Key Responsibilities

- **Linguistic Analysis:** Uses the `spaCy` library to perform deep linguistic parsing of each question.
- **Question Simplification:** Implements a multi-stage pipeline to rewrite complex questions into simpler forms.
- **Structural Formalization:** Transforms the simplified question into a structured **Subject-Predicate-Object (SPO)** triplet.
- **Ontology Mapping:** Maps the formalized SPO triplet to a specific provenance relationship defined in its internal knowledge base.
- **Transparent Reporting:** Returns a detailed JSON object for each question, showing the output of every stage in the pipeline.

!> **Proof-of-Concept & Extensibility**

> The current implementation of the PQ Analyzer is a powerful proof-of-concept. While it successfully handles a set of core question patterns, its real strength lies in its modular and extensible design. The documentation below focuses heavily on how a developer can easily add new patterns, rules, and mappings to expand its capabilities.

---

## The 5-Stage Analysis Pipeline

For each question it receives, the service executes a 5-stage pipeline:

1.  **Linguistic Parsing (`spaCy`)**: The raw question is processed to create a `Doc` object with rich linguistic annotations.
2.  **Pattern Checking (`pattern_checker`)**: The `Doc` is checked against a list of registered linguistic patterns to find specific grammatical structures.
3.  **Question Rewriting (`question_rewriter`)**: Based on the patterns found, the question is programmatically rewritten into a simpler form.
4.  **Question Formalization (`question_formalizer`)**: The rewritten question is transformed into a structured SPO triplet (e.g., `('who', 'generate', 'data')`).
5.  **PROV-O Mapping (`prov_mapper`)**: The SPO triplet is mapped to a specific provenance concept using a rules-based system.

---

## For Developers (Living Documentation)

### Development & Build Setup

The service is designed for easy local testing. Most modules contain a `if __name__ == "__main__:` block, allowing them to be run in isolation.

**Local Server:**

```bash
# This will start the Flask server
python main.py
```

**Docker Build Process:**
The `Dockerfile` creates a lightweight production image by installing dependencies and the `spaCy` model into a slim Python base image.

<details>
<summary><b>Click to see the full Dockerfile</b></summary>

```dockerfile
FROM python:3.11-slim
WORKDIR /app
COPY . /app
RUN pip install --no-cache-dir -r requirements.txt
RUN python -m spacy download en_core_web_sm
CMD ["python", "main.py"]
```

</details>

### Extending the Analyzer (1): Adding New Rewrite Patterns

The question rewriting logic (Stages 2 and 3) is driven by a flexible registration mechanism. To add a new rule for simplifying questions, a developer follows a two-step process.

#### The Pattern Registration Mechanism

The `pattern_checker.py` module contains a central registry:

```python
registered_patterns = [
    ("verb_then_pcomp", check_for_verb_followed_by_pcomp)
]
```

This is a list of tuples, where each tuple represents a single pattern:

- **`"verb_then_pcomp"`**: A unique string name for the pattern.
- **`check_for_verb_followed_by_pcomp`**: A direct reference to the Python function that implements the logic for finding this pattern.

The `check_for_common_patterns` function iterates through this list and calls each registered function, collecting any matches.

#### Step-by-Step Guide to Add a New Pattern

1.  **Write the Handler Function:** In `pattern_checker.py`, create a new Python function that accepts a spaCy `Doc` object. This function must contain the logic to find your specific linguistic pattern. If the pattern is found, it must return a tuple containing the information needed for rewriting. If not, it must return `None`.

2.  **Register the New Handler:** Add a new entry to the `registered_patterns` list, providing a unique name for your pattern and a reference to the function you just created.

    ```python
    # In pattern_checker.py
    def my_new_pattern_handler(tokens):
        # ... your logic here ...
        if pattern_is_found:
            return (token_indices, "some_info", "other_info")
        return None

    registered_patterns = [
        ("verb_then_pcomp", check_for_verb_followed_by_pcomp),
        ("my_new_pattern", my_new_pattern_handler) # Add your new pattern here
    ]
    ```

3.  **Add the Rewrite Logic:** In `question_rewriter.py`, add an `if` condition to handle your new pattern by name and call a corresponding function to perform the actual string manipulation.

### Extending the Analyzer (2): Adding New Ontology Mappings

The `prov_mapper.py` module (Stage 5) also uses a configurable knowledge base to map the formalized SPO triplet to a PROV-O concept.

#### The `concepts` Data Structure

The `concepts` list is the core of the mapping engine. Each entry is a tuple defining a complete provenance relationship pattern:
`(ID, Category, Question Word, Target Verb, Input Type, Middle Type, Output Type)`

Using `(0, "accountability", "who", "wasAttributedTo", "prov:Entity", "prov:Activity", "prov:Agent")` as an example:

- **`Question Word` ("who"):** Matches the **Subject** of the SPO triplet.
- **`Input Type` ("prov:Entity"):** The expected type of the **Object** of the SPO triplet.
- **`Middle Type` ("prov:Activity"):** The expected type of the **Predicate** of the SPO triplet.
- **`Output Type` ("prov:Agent"):** The expected type of the final answer (e.g., a "who" question expects an Agent).
- **`Target Verb` ("wasAttributedTo"):** The formal PROV-O property this concept maps to.

#### The `associations` Dictionary

This dictionary provides the crucial link between the informal verb (Predicate) in the user's question and the formal `Target Verb` from the `concepts` list.

```python
associations = {
    "wasAttributedTo" : [ "is responsible", "is attributed" ],
    "wasAssociatedWith" : [ "is responsible", "is associated" ],
    "generatedAtTime" : [ "generated" ],
    "entityUsed" : [ "used" ],
    # ...
}
```

To handle more verbs, simply add them to the list for the corresponding `Target Verb`. The mapper uses this dictionary to disambiguate when a question word like "who" could map to multiple concepts.

?> **Future Work: External Configuration**

> For improved maintainability and to allow non-programmers to contribute, the `concepts` list and `associations` dictionary are prime candidates for being extracted into external configuration files (e.g., JSON or YAML) in future versions.

### API Endpoint

#### `POST /api/v1/pq-analyzer`

- **Request:** `multipart/form-data` with a file part named `file`.
- **Success Response:** `200 OK` with a JSON array where each object is a detailed report of one question's analysis.
