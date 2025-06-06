import json
import token_to_components

def unify_ai_system_with_pqs(ai_system_data, pq_data):

    """
    - Map concepts to concrete instances of the AI system (Activity to Step, Search for entity in target Activity)
    - No confusion: OK, Confusion: Ask user for input / Clarify
    - Extend current trace template for target Activity with new required fields
    - SPARQL Query generation
    """

    mapping = []

    for pq in pq_data:
        mapping.append(unify_one_pq_with_ai_system(ai_system_data, pq))

    return mapping

def unify_one_pq_with_ai_system(ai_system_data, pq):
    if pq["target_concept"] is None:
        return None

    concept = pq["target_concept"][1].replace(" ", "_")+"_"+pq["target_concept"][3]
    token_filled = token_to_components.select_correct_handler(concept, pq, ai_system_data)

    return None


if __name__ == "__main__":
    with open("sample/ai_system_analyzer_response.json", "r") as file:
        ai_system_input = json.load(file)

    with open("sample/pq_analyzer_response.json", "r") as file:
        pq_data_input = json.load(file)

    unified_result = unify_ai_system_with_pqs(ai_system_input, pq_data_input)
    print(unified_result)