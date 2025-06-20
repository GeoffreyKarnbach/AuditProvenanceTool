import json
import token_to_components
import ai_system_extractor

def unify_ai_system_with_pqs(ai_system_data, pq_data, request_id):

    """
    - Map concepts to concrete instances of the AI system (Activity to Step, Search for entity in target Activity)
    - No confusion: OK, Confusion: Ask user for input / Clarify
    - Extend current trace template for target Activity with new required fields
    - SPARQL Query generation
    """

    mapping = []

    for pq in pq_data:
        res = unify_one_pq_with_ai_system(ai_system_data, pq)
        if res is not None:
            mapping.append(res)
        else:
            print(f"Skipping PQ: {pq['original_question']} due to missing target concept.")

    response = {
        "options": ai_system_extractor.get_possible_activities_with_entities(ai_system_data),
        "mappingSuggestions": mapping,
        "requestId": request_id
    }

    return response

def unify_one_pq_with_ai_system(ai_system_data, pq):
    if pq["target_concept"] is None:
        return None

    concept = pq["target_concept"][1].replace(" ", "_")+"_"+pq["target_concept"][3]
    token_filled = token_to_components.select_correct_handler(concept, pq, ai_system_data)

    return {
        "question": pq["original_question"],
        "mapping": token_filled
    }


if __name__ == "__main__":
    with open("sample/ai_system_analyzer_response.json", "r") as file:
        ai_system_input = json.load(file)

    with open("sample/pq_analyzer_response.json", "r") as file:
        pq_data_input = json.load(file)

    unified_result = unify_ai_system_with_pqs(ai_system_input, pq_data_input, "test_request_id")
    print(unified_result)