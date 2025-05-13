from flask import Flask, request
import time

app = Flask(__name__)

@app.route("/api/v1/pq-analyzer", methods=["POST"])
def pq_analyzer():
    uploaded_file = request.files.get("file")

    if uploaded_file and uploaded_file.filename.endswith(".txt"):
        content = uploaded_file.read().decode("utf-8")
        print(content)
        time.sleep(10)
        return "OK", 200
    
    return "Invalid file", 400

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5501)