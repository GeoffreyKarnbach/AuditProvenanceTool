from flask import Flask, request, Response
from flask_cors import CORS

import sparql_query_generation
import unifier
import json
import trace_template_generation
import output_util

application = Flask(__name__)
CORS(application)

ai_system_data_request_cache = {}
pq_data_request_cache = {}

@application.route("/api/v1/unification-first-step/<request_id>", methods=["POST"])
def unification_process_init(request_id):
    ai_system_analyzer_description = request.files.get("ai_system_analyzer")
    pq_analyzer_response = request.files.get("pq_analyzer")

    if ai_system_analyzer_description and ai_system_analyzer_description.filename.endswith(".json") and \
         pq_analyzer_response and pq_analyzer_response.filename.endswith(".json"):

        ai_system_content = ai_system_analyzer_description.read().decode("utf-8")
        pq_content = pq_analyzer_response.read().decode("utf-8")

        ai_system_data = json.loads(ai_system_content)
        pq_data = json.loads(pq_content)

        ai_system_data_request_cache[request_id] = ai_system_data
        pq_data_request_cache[request_id] = pq_data

        result = unifier.unify_ai_system_with_pqs(ai_system_data, pq_data, request_id)
        json_response = json.dumps(result, ensure_ascii=False, sort_keys=False)

        print(json_response)

        return Response(json_response, status=200, mimetype='application/json')

    return "Invalid request", 400

@application.route("/api/v1/unification-response/<request_id>", methods=["POST"])
def unification_process_response(request_id):
    print("Unification process response endpoint called with request_id:", request_id)

    ai_system_data_cached = ai_system_data_request_cache.get(request_id)
    pq_data_cached = pq_data_request_cache.get(request_id)

    if not ai_system_data_cached or not pq_data_cached:
        return Response(
            json.dumps({"error": "No cached data found for the given request_id"}),
            status=404,
            mimetype='application/json'
        )

    response = request.get_json()
    trace_templates = trace_template_generation.generate_trace_templates(response, ai_system_data_cached, pq_data_cached)
    sparql_queries = sparql_query_generation.generate_sparql_queries(trace_templates, response, ai_system_data_cached, pq_data_cached)
    zip_file_path = output_util.build_output(trace_templates, sparql_queries)


    with open(zip_file_path, 'rb') as zip_file:
        zip_content = zip_file.read()
        return Response(zip_content, status=200, mimetype='application/zip', headers={
            'Content-Disposition': f'attachment; filename=output.zip'
        })

if __name__ == "__main__":
    application.run(host="0.0.0.0", port=5504, debug=True)