def map_formalized_question_to_prov_o(question):

    potential_matching_concepts = get_concept_match_for_question_word(question[0])
    print(potential_matching_concepts)

    input_type = get_input_type(is_prov_agent(question[2]), is_prov_activity(question[2]), is_prov_entity(question[2]))

    target_concept = None
    for concept in potential_matching_concepts:
        if concept[4] == input_type:
            target_concept = concept
            break

    print(f"Target concept: {target_concept}")

    return question

def is_prov_agent(item):
    return False

def is_prov_activity(item):
    for term in activity_terms:
        if term in item.lower():
            return True

    return False

def is_prov_entity(item):
    for term in entity_terms:
        if term in item.lower():
            return True

    return False

def get_input_type(is_agent, is_activity, is_entity):
    if is_agent:
        return "prov:Agent"
    elif is_activity:
        return "prov:Activity"
    elif is_entity:
        return "prov:Entity"
    else:
        return None

def get_concept_match_for_question_word(qword):
    matching_concepts = []
    for concept in concepts:
        if concept[2] == qword:
            matching_concepts.append(concept)

    return matching_concepts

# ID, Category, Question Word, Relation Pattern, Input Type, Output Type
concepts = [
    (0, "accountability", "who", "wasAttributedTo", "prov:Entity", "prov:Agent"),
    (1, "accountability", "who", "wasAssociatedWith", "prov:Activity", "prov:Agent"),
    #(2, "accountability", "which", "actedOnBehalfOf", "prov:Agent", "prov:Agent"),
    (3, "temporal provenance", "when", "startedAtTime", "prov:Activity", "timestamp"),
    (4, "temporal provenance", "when", "endedAtTime", "prov:Activity", "timestamp"),
    (5, "temporal provenance", "when", "generatedAtTime", "prov:Entity", "timestamp"),
    (6, "usage", "what", "entityUsed", "prov:Activity", "prov:Entity"),
    (7, "generation", "what", "activityGenerated", "prov:Entity", "prov:Activity"),
    (8, "location", "where", "atLocation", "prov:Entity", "location"),
    #(9, "derivation", "which", "wasDerivedFrom", "prov:Entity", "prov:Entity"),
    #(10, "global provenance", "what", "selectAllActivities", None, "prov:Activity"),
    (11, "aggregation", "how often", "occurrenceCount", "prov:Entity", "number"),
]

associations = {
    "wasAttributedTo" : [ "is responsible", "is attributed" ],
    "wasAssociatedWith" : [ "is responsible", "is associated" ],
    "startedAtTime" : [ "started" ],
    "endedAtTime" : [ "ended" ],
    "generatedAtTime" : [ "generated" ],
    "entityUsed" : [ "used" ],
    "activityGenerated" : [ "generated" ],
    "atLocation" : [ "located" ],
}

activity_terms = [
    "activity",
    "process",
    "task",
    "operation",
    "event",
    "action",
    "generation"
]

entity_terms = [
    "entity",
    "data",
    "dataset",
    "model",
    "information",
    "record",
    "file",
    "document",
    "artifact",
    "object",
    "license",
    "guideline"
]