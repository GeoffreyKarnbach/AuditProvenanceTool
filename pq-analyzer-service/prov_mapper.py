def map_formalized_question_to_prov_o(question):

    potential_matching_concepts = get_concept_match_for_question_word(question[0])
    print(potential_matching_concepts)

    input_type = get_input_type(is_prov_agent(question[2]), is_prov_activity(question[2]), is_prov_entity(question[2]))

    print(input_type)
    target_concept = []
    for concept in potential_matching_concepts:
        if concept[4] == input_type or concept[4] == "variable":
            target_concept.append(concept)

    if len(target_concept) == 0:
        target_concept = None
    elif len(target_concept) == 1:
        target_concept = target_concept[0]
    else:
        target_concept = select_concept_on_association(target_concept, question)


    print(f"Target concept: {target_concept}")
    if target_concept is None:
        print("No matching concept found for the question.")
        return None, None

    mapped_question = {
        question[0]: target_concept[6],
        question[1]: target_concept[5],
        question[2]: target_concept[4],
    }

    return target_concept, mapped_question

def is_prov_agent(item):
    return False

def is_prov_activity(item):
    # TODO: Add maximum character deviation
    for term in activity_terms:
        if term in item.lower():
            return True

    return False

def is_prov_entity(item):
    ## TODO: Add maximum character deviation
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

def select_concept_on_association(possible_concepts, question):
    # Check if question[1] and concepts[4] match through the association dictionary
    for concept in possible_concepts:
        if question[1] in associations.get(concept[3], []):
            return concept

    # If no match found, return the first concept as a fallback
    return possible_concepts[0] if possible_concepts else None

# ID, Category, Question Word, Target Verb, Relation Pattern, Input Type, Middle Type, Output Type
concepts = [
    (0, "accountability", "who" ,"wasAttributedTo", "prov:Entity", "prov:Activity" , "prov:Agent"),
    (1, "accountability", "who",  "wasAssociatedWith", "prov:Activity", "prov:Activity", "prov:Agent"),
    #(2, "accountability", "which", "actedOnBehalfOf", "prov:Agent", "prov:Agent"),
    (3, "temporal provenance", "when","startedAtTime", "prov:Activity", "specification", "timestamp"),
    (4, "temporal provenance", "when", "endedAtTime", "prov:Activity", "specification" ,"timestamp"),
    (5, "temporal provenance", "when", "generatedAtTime", "prov:Entity", "specification" , "timestamp"),
    (6, "usage", "what", "entityUsed", "prov:Activity", "prov:Activity" , "prov:Entity"),
    (7, "generation", "what", "activityGenerated", "prov:Entity", "prov:Activity" ,"prov:Activity"),
    (8, "location", "where", "atLocation", "prov:Entity", "specification" ,"location"),
    #(9, "derivation", "which", "wasDerivedFrom", "prov:Entity", "prov:Entity"),
    #(10, "global provenance", "what", "selectAllActivities", None, "prov:Activity"),
    (11, "aggregation", "how often","occurrenceCount", "prov:Entity", None ,"number"),
    (12, "selection", "what", "selectionOfEntity", "prov:Entity", None, "prov:Entity"),
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
    "occurrenceCount" : [ "occurred", "happened" ],
    "selectionOfEntity": [ "is", "be"],
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
    "guideline",
    "source code"
]