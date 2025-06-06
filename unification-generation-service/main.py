from flask import Flask, request
import unifier
import json
application = Flask(__name__)

@application.route("/api/v1/unification", methods=["POST"])
def unification_process_init():
    ai_system_analyzer_description = request.files.get("ai_system_analyzer")
    pq_analyzer_response = request.files.get("pq_analyzer")

    # Need all to be json files

    if ai_system_analyzer_description and ai_system_analyzer_description.filename.endswith(".json") and \
         pq_analyzer_response and pq_analyzer_response.filename.endswith(".json"):

        ai_system_content = ai_system_analyzer_description.read().decode("utf-8")
        pq_content = pq_analyzer_response.read().decode("utf-8")

        ai_system_data = json.loads(ai_system_content)
        pq_data = json.loads(pq_content)

        return unifier.unify_ai_system_with_pqs(ai_system_data, pq_data)

    return "Invalid request", 400


if __name__ == "__main__":
    application.run(host="0.0.0.0", port=5504, debug=True)