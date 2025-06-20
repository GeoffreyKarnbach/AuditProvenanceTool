from typing import Dict, Any, List

def get_possible_activities_with_entities(ai_system_data):
    activities_with_entities = []
    for step in ai_system_data.get("workflowSteps", []):
        entities = []
        for var in step.get("workflowInputVariables", []):
            entities.append([var.get("id")] + var.get("defaultTraceFields", []))
        for var in step.get("workflowOutputVariables", []):
            entities.append([var.get("id")] + var.get("defaultTraceFields", []))

        activities_with_entities.append({
            "activity": step["id"],
            "entities": entities
        })

    return activities_with_entities