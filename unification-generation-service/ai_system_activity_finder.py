from rapidfuzz import fuzz

def find_matching_activity(ai_system_data, keyword):
    matching_activities = []

    # First we look for subtypes that match the keyword and collect activities for those subtypes
    matching_subtypes = []
    for subtype, activities in keyword_to_activity_subtype.items():
        if any(
                keyword.lower() in act.lower() or act.lower() in keyword.lower()
                for act in activities
        ):
            matching_subtypes.append(subtype)

    for subtype in matching_subtypes:
        activities = get_all_activities_with_subtype(ai_system_data, subtype)
        if activities:
            matching_activities.extend(activities)

    # We also look for activities that match the keyword directly inside their name or id
    matching_activities.extend(get_all_activities_with_keyword(ai_system_data, keyword))

    # Remove duplicates
    return list(set(matching_activities))

def get_all_activities_with_subtype(ai_system_data, subtype):
    activities = []
    for activity in ai_system_data.get("workflowSteps", []):
        if activity.get("stepSubtype").endswith(subtype):
            activities.append(activity["id"])
    return activities

def get_all_activities_with_keyword(ai_system_data, keyword, threshold=90):
    activities = []
    for activity in ai_system_data.get("workflowSteps", []):
        step_name = activity.get("stepName", "")
        step_id = activity.get("id", "")
        if (
            fuzz.partial_ratio(keyword.lower(), step_name.lower()) >= threshold
            or fuzz.partial_ratio(keyword.lower(), step_id.lower()) >= threshold
        ):
            activities.append(activity["id"])
    return activities


keyword_to_activity_subtype = {
    "ns#Generation": [
        "generate",
        "produce"
    ],
    "ns#Transformation": [
        "transform",
        "process",
        "analyze",
    ],
    "ns#Inference": [
        "infer",
        "predict",
        "classify",
        "detect",
        "identify",
        "run",
        "produce"
    ],
    "ns#Training": [
        "train",
        "fine-tune",
        "optimize",
        "adapt"
    ]
}