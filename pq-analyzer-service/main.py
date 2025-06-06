from flask import Flask, request, Response
import question_handler
import json

application = Flask(__name__)

@application.route("/api/v1/pq-analyzer", methods=["POST"])
def pq_analyzer():
    uploaded_file = request.files.get("file")

    if uploaded_file and uploaded_file.filename.endswith(".txt"):
        content = uploaded_file.read().decode("utf-8")
        result = question_handler.handle_questions(content)

        json_response = json.dumps(result, ensure_ascii=False, sort_keys=False)

        return Response(json_response, status=200, mimetype='application/json')
    
    return "Invalid file", 400

if __name__ == "__main__":
    application.run(host="0.0.0.0", port=5501, debug=True)