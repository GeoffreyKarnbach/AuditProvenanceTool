# Architecture / Workflow

## Step 1 - Frontend Input

In this step, the user will be able to import, using drag and drop input 2 files, a TTL file, containing the description of the AI system, and a TXT file, containing the provenance questions, one per line.

The frontend will then trigger both on the AI system analyzer (Step 2A) and the PQ analyzer (Step 2B) a workflow, that will be displayed in a progress bar, for each input. Internally, the data will be sent to the public endpoint of Step 2A, receive a session ID, that will be used to track the current state.

## Step 2A - AI System Analyzer

The AI System Analyzer will convert the AI System, described in the TTL file into an internal representation of Java classes.

Each Step will have its own java class instance, with actors, inputs and outputs being attributes of the Java classes. Furthermore, the Step Java Class will contain metadata / internal processing fields, that will allow it to later on track the required stack trace entries needed for this step.

The entire structure will be persisted in a "managing" class, that will contain further data.

During Step 2A, different analyzation and preprocessing of the AI System can already take place and be saved inside of the "managing" class, to facilitate later steps.

The model will be persisted inside of a small DB, such as an embedded H2 instance, or a SQlite instance. The service will be implemented using Java and SpringBoot.

## Step 2B - PQ Analyzer

In this step, the list of PQ will be analyzed, according to the paper (natural language PQ, liguistic analysis, rewritten PQ, ...). These steps will all generate data, that will be stored for each question inside of a JSON object.

The rewriting step of the workflow may be supported by LLMs for complex PQs.

The processed PQs are sent back to the java service for Step 3.
This service will be implemented using Python and different modules to handle NLP.

## Step 3 - Unification + Clarification

Unify AI System model with processed PQs.

For each step, add trace template items to class field.

In case some input is not clearly defined, we request the user for clarification in the frontend.

## Step 4 - Generation

Generate the Trace Templates for each step ( = Java Class) and SPARQL queries for each PQ
