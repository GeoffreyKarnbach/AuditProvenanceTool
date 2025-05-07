from flask import Flask, request

app = Flask(__name__)

@app.route("/api/v1/pq-analyzer", methods=["POST"])
def pq_analyzer():
    uploaded_file = request.files.get("file")
    if uploaded_file and uploaded_file.filename.endswith(".txt"):
        # Print the content
        content = uploaded_file.read().decode("utf-8")
        print(content)
        return "OK", 200
    return "Invalid file", 400

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5501)