import json
import trace_template_generation
from string import Template

accountability_wasAttributedTo_template = Template("""
PREFIX prov: <http://www.w3.org/ns/prov#>

SELECT ?who
WHERE {
  $activity prov:wasAssociatedWith ?who .
}
""")

def generate_sparql_queries(trace_templates, unification_response, ai_system_data, pq_data):
    queries = {}
    for item in unification_response["items"]:
        question = item["question"]
        pq_type = None
        for pq in pq_data:
            if pq["original_question"] == question:
                pq_type = pq["target_concept"][1] + "_" + pq["target_concept"][3]
                break

        queries[question] = mapping_functions.get(pq_type)(trace_templates, item)

    return queries

def sparql_accoutability_wasAttributedTo(trace_templates, question):
    return accountability_wasAttributedTo_template.substitute(activity=question["selectedActivityId"])

mapping_functions = {
    "accountability_wasAttributedTo": sparql_accoutability_wasAttributedTo,
    "accountability_wasAssociatedWith": None,
    "temporal_provenance_startedAtTime": None,
    "temporal_provenance_endedAtTime": None,
    "temporal_provenance_generatedAtTime": None,
    "usage_entityUsed": None,
    "generation_activityGenerated": None,
    "location_atLocation": None,
    "aggregation_occurrenceCount": None,
    "selection_selectionOfEntity": None,
}

if __name__ == "__main__":
    with open("sample/ai_system_analyzer_response.json", "r") as file:
        ai_system_input = json.load(file)

    with open("sample/pq_analyzer_response.json", "r") as file:
        pq_data_input = json.load(file)

    with open("sample/frontend_unification_response.json", "r") as file:
        unification_data_response = json.load(file)

    trace_templates = trace_template_generation.generate_trace_templates(unification_data_response,ai_system_input,pq_data_input)
    sparql_queries = generate_sparql_queries(trace_templates, unification_data_response, ai_system_input, pq_data_input)

    print("Unified result:", trace_templates)
    print("Generated SPARQL queries:", sparql_queries)