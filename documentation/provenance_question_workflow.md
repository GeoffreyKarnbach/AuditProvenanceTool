# Implementing Provenance Question Transformation in Python

This document outlines a simple Python-based implementation of the transformation steps from a natural language provenance question (PQ) to a structured PROV-O-compatible representation. It is inspired by typical provenance analysis pipelines and shows how tools like spaCy and RDFLib can be used to achieve this.

---

## 1. Natural Language PQ

We start with a basic provenance question written in natural language.

```python
pq = "Who was involved in generating the data?"
```

---

## 2. Linguistic Analysis of the PQ

To analyze the structure of the question, we can use [spaCy](https://spacy.io/). It allows us to identify parts of speech and syntactic dependencies.

```python
import spacy

nlp = spacy.load("en_core_web_sm")
doc = nlp(pq)

for token in doc:
    print(f"{token.text:<12} POS: {token.pos_:<10} DEP: {token.dep_}")
```

This helps extract:
- Subject (`Who`)
- Verb (`involved`)
- Object or complement (`generating the data`)

---

## 3. Rewritten PQ

Based on the linguistic structure, we can manually or heuristically simplify the question to a more direct form:

```python
rewritten = "Who generated the data?"
```

This makes it easier to formalize in the next step.

---

## 4. Formalization of the PQ

We now identify the formal elements (subject, predicate, object) that correspond to PROV concepts.

```python
formalized = ("who", "generated", "data")
```

This can be done using a template or via rule-based extraction depending on the complexity of the question.

---

## 5. Mapping to PROV-O

Using standard PROV-O vocabulary, we map the formalized elements to RDF classes and properties.

```python
prov_mapping = {
    "who": "prov:Agent",
    "generated": "prov:Activity",
    "data": "prov:Entity"
}
```

---

## 6. Provenance Trace Template

We define a template that will be used to generate RDF triples. This includes placeholders for common properties like `prov:wasGeneratedBy`, `prov:wasAssociatedWith`, etc.

```python
trace_template = {
    "id": "<unique-id>",
    "creator": "<agent-name>",
    # More fields as needed
}
```

---

## 7. Provenance Trace (Example)

Assuming we have a specific data source and agent, we can fill in the template accordingly:

```python
trace_instance = {
    "id": "MIMIC_III_Database_2016",
    "creator": "MIT Lab"
}
```

---

## 8. Provenance Result

Finally, we output or serialize the result. If working with RDF, `rdflib` is a good choice:

```python
from rdflib import Graph, Namespace, URIRef, Literal
from rdflib.namespace import RDF, XSD

PROV = Namespace("http://www.w3.org/ns/prov#")

g = Graph()
g.bind("prov", PROV)

activity = URIRef("http://example.org/activity/generation")
agent = URIRef("http://example.org/agent/MIT_Lab")
entity = URIRef("http://example.org/entity/MIMIC_III_Database_2016")

g.add((entity, PROV.wasGeneratedBy, activity))
g.add((activity, PROV.wasAssociatedWith, agent))
g.add((agent, RDF.type, PROV.Agent))

print(g.serialize(format="turtle").decode())
```

This will produce RDF triples linking the activity, agent, and entity based on the question.

---

## Summary

This workflow transforms natural language provenance questions into structured, machine-interpretable provenance data. While basic rule-based approaches can work for simple questions, more advanced cases may require semantic parsing or ML-based question interpretation.
