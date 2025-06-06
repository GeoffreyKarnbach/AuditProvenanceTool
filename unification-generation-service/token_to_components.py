import ai_system_activity_finder
import ai_system_entity_finder

def select_correct_handler(concept, pq, ai_system_data):
    for name, handler in mapping_functions:
        if name == concept:
            print("*"*40)
            return handler(pq, ai_system_data)
    return None

def handle_accoutability_wasAttributedTo(pq, ai_system_data):
    print("Concept: accountability_wasAttributedTo")
    print(pq["original_question"])

    pq_prov_mapped = list(pq["mapped_to_prov_o"].items())
    if pq_prov_mapped is None:
        return None

    # This will be the target return type, for this concept it is a prov:Agent
    qword, response_type = pq_prov_mapped[0]

    # Now we target an activity for the second element
    activity_word, token_type = pq_prov_mapped[1]
    matching_activity_id = ai_system_activity_finder.find_matching_activity(ai_system_data, activity_word)

    # Now we target entities for the third element
    entity_word, entity_type = pq_prov_mapped[2]

    for activity_id in matching_activity_id:
        matching_entities = ai_system_entity_finder.find_matching_entity_for_activity(
            ai_system_data, activity_id, entity_word
        )
        print("Matching entities for activity:", activity_id, "->", matching_entities)

    return None

def handle_accoutability_wasAssociatedWith(pq, ai_system_data):
    print("Concept: accountability_wasAssociatedWith")
    return None

def handle_temporal_provenance_startedAtTime(pq, ai_system_data):
    print("Concept: temporal_provenance_startedAtTime")
    return None

def handle_temporal_provenance_endedAtTime(pq, ai_system_data):
    print("Concept: temporal_provenance_endedAtTime")
    return None

def handle_temporal_provenance_generatedAtTime(pq, ai_system_data):
    print("Concept: temporal_provenance_generatedAtTime")
    return None

def handle_usage_entityUsed(pq, ai_system_data):
    print("Concept: usage_entityUsed")
    return None

def handle_generation_activityGenerated(pq, ai_system_data):
    print("Concept: generation_activityGenerated")
    return None

def handle_location_atLocation(pq, ai_system_data):
    print("Concept: location_atLocation")
    return None

def handle_aggregation_occurrenceCount(pq, ai_system_data):
    print("Concept: aggregation_occurrenceCount")
    return None

def handle_selection_selectionOfEntity(pq, ai_system_data):
    print("Concept: selection_selectionOfEntity")
    return None


mapping_functions = [
    ("accountability_wasAttributedTo", handle_accoutability_wasAttributedTo),
    ("accountability_wasAssociatedWith", handle_accoutability_wasAssociatedWith),
    ("temporal_provenance_startedAtTime", handle_temporal_provenance_startedAtTime),
    ("temporal_provenance_endedAtTime", handle_temporal_provenance_endedAtTime),
    ("temporal_provenance_generatedAtTime", handle_temporal_provenance_generatedAtTime),
    ("usage_entityUsed", handle_usage_entityUsed),
    ("generation_activityGenerated", handle_generation_activityGenerated),
    ("location_atLocation", handle_location_atLocation),
    ("aggregation_occurrenceCount", handle_aggregation_occurrenceCount),
    ("selection_selectionOfEntity", handle_selection_selectionOfEntity),
]
