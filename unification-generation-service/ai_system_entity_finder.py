from rapidfuzz import fuzz

def find_matching_entity_for_activity(ai_system_data, activity_id, keyword):
    matching_entities = []

    # First we look for subtypes that match the keyword and collect activities for those subtypes
    matching_subtypes = []
    for subtype, entity in keyword_to_entity_subtype.items():
        if any(
                keyword.lower() in act.lower() or act.lower() in keyword.lower()
                for act in entity
        ):
            matching_subtypes.append(subtype)


    for subtype in matching_subtypes:
        entities = get_all_entities_with_subtype(ai_system_data, subtype, activity_id)
        if entities:
            matching_entities.extend(entities)

    # We also look for activities that match the keyword directly inside their name or id
    matching_entities.extend(get_all_entities_with_keyword(ai_system_data, keyword, activity_id))

    matching_entities = list(set(matching_entities))

    # Transform array of IDs into array of arrays, where the first element is the ID and the rest are trace options
    result = [[entity_id] + aggregate_entity_with_trace_options(entity_id, ai_system_data) for entity_id in matching_entities]

    return  result

def get_all_entities_with_subtype(ai_system_data, subtype, activity_id):
    entities = []
    matching_activities = get_activities_from_ids(ai_system_data, activity_id)

    for activity in matching_activities:
        for entity in activity["workflowInputVariables"]:
            if entity["variableSubtype"] and entity["variableSubtype"].endswith(subtype):
                entities.append(entity["id"])

    for activity in matching_activities:
        for entity in activity["workflowOutputVariables"]:
            if entity["variableSubtype"] and entity["variableSubtype"].endswith(subtype):
                entities.append(entity["id"])

    return entities

def get_all_entities_with_keyword(ai_system_data, keyword, activity_id, threshold=90):
    entities = []
    matching_activities = get_activities_from_ids(ai_system_data, activity_id)

    keywords = keyword.lower().split()

    for activity in matching_activities:
        for entity in activity["workflowInputVariables"]:
            entity_name = entity.get("name", "").lower()
            entity_id = entity.get("id", "").lower()
            if any(
                    fuzz.partial_ratio(word, entity_name) >= threshold or
                    fuzz.partial_ratio(word, entity_id) >= threshold
                    for word in keywords
            ):
                entities.append(entity["id"])

    for activity in matching_activities:
        for entity in activity["workflowOutputVariables"]:
            entity_name = entity.get("name", "").lower()
            entity_id = entity.get("id", "").lower()
            if any(
                    fuzz.partial_ratio(word, entity_name) >= threshold or
                    fuzz.partial_ratio(word, entity_id) >= threshold
                    for word in keywords
            ):
                entities.append(entity["id"])

    return entities

def get_activities_from_ids(ai_system_data, activity_ids):
    activities = []
    for activity in ai_system_data.get("workflowSteps", []):
        if activity["id"] in activity_ids:
            activities.append(activity)
    return activities

def aggregate_entity_with_trace_options(entity_id, ai_system_data):
    for variable in ai_system_data.get("workflowVariables", []):
        if variable["id"] == entity_id:
            return variable.get("defaultTraceFields", [])

    return []

keyword_to_entity_subtype = {
    "ns#Processor":[
        "tool",
        "processor",
        "engine",
        "module",
        "component",
        "pipeline",
        "system"
    ],
    "ns#TrainingDataset": [
        "dataset",
        "data",
        "training",
        "raw",
        "corpus",
        "samples",
        "input",
        "records",
        "collection"
    ],
    "ns#StatisticalModel": [
        "model",
        "algorithm",
        "network",
        "classifier",
        "regressor",
        "predictor"
    ],
    "ns#MLInferenceResult": [
        "result",
        "inference",
        "prediction",
        "classification",
        "detection",
        "output",
        "estimate",
        "score",
        "label"
    ],
}