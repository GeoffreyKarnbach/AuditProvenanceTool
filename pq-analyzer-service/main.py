from flask import Flask, request
import question_handler

application = Flask(__name__)

@application.route("/api/v1/pq-analyzer", methods=["POST"])
def pq_analyzer():
    uploaded_file = request.files.get("file")

    if uploaded_file and uploaded_file.filename.endswith(".txt"):
        content = uploaded_file.read().decode("utf-8")
        return question_handler.handle_questions(content), 200
    
    return "Invalid file", 400

if __name__ == "__main__":
    application.run(host="0.0.0.0", port=5501, debug=True)