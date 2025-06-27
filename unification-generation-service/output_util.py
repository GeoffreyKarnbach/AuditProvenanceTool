import json
import trace_template_generation
import zipfile
import os
import sparql_query_generation

def generate_output_trace_files(trace_templates):

    output_dir = "output/traces"
    if os.path.exists(output_dir):
        for file in os.listdir(output_dir):
            file_path = os.path.join(output_dir, file)
            if os.path.isfile(file_path):
                os.remove(file_path)
        os.rmdir(output_dir)

    os.makedirs(output_dir, exist_ok=True)

    for filename, content in trace_templates.items():
        # Convert the content to JSON string
        json_content = json.dumps(content, ensure_ascii=False, indent=4)

        # Write the JSON string to a file
        with open(os.path.join(output_dir, filename), "w", encoding="utf-8") as file:
            file.write(json_content)

    print(f"Output trace folder created: output/traces with {len(trace_templates)} templates.")

def generate_output_sparql_files(sparql_queries):
    output_dir = "output/sparql"
    if os.path.exists(output_dir):
        for file in os.listdir(output_dir):
            file_path = os.path.join(output_dir, file)
            if os.path.isfile(file_path):
                os.remove(file_path)
        os.rmdir(output_dir)

    os.makedirs(output_dir, exist_ok=True)

    counter = 1

    for question, query in sparql_queries.items():
        # Write the SPARQL query to a file
        filename = f"query_{counter}"
        with open(os.path.join(output_dir, filename + ".sparql"), "w", encoding="utf-8") as file:
            file.write(f"# {question}\n")
            file.write(query)

        counter += 1

    print(f"Output SPARQL folder created: output/sparql with {len(sparql_queries)} queries.")

def generate_output_zip_file():
    with zipfile.ZipFile("output/output.zip", "w") as zip_file:
        for foldername, subfolders, filenames in os.walk("output/traces"):
            for filename in filenames:
                file_path = os.path.join(foldername, filename)
                arcname = os.path.relpath(file_path, "output")  # damit "traces/..." im ZIP steht
                zip_file.write(file_path, arcname)

        for foldername, subfolders, filenames in os.walk("output/sparql"):
            for filename in filenames:
                file_path = os.path.join(foldername, filename)
                arcname = os.path.relpath(file_path, "output")
                zip_file.write(file_path, arcname)

def clean_up():
    output_dir = "output/traces"
    if os.path.exists(output_dir):
        for file in os.listdir(output_dir):
            file_path = os.path.join(output_dir, file)
            if os.path.isfile(file_path):
                os.remove(file_path)
        os.rmdir(output_dir)

    # Remove sparql directory if it exists
    sparql_dir = "output/sparql"
    if os.path.exists(sparql_dir):
        for file in os.listdir(sparql_dir):
            file_path = os.path.join(sparql_dir, file)
            if os.path.isfile(file_path):
                os.remove(file_path)
        os.rmdir(sparql_dir)

    print(f"Cleaned up output directory: {output_dir}")

def build_output(trace_templates, sparql_queries):
    generate_output_trace_files(trace_templates)
    generate_output_sparql_files(sparql_queries)
    generate_output_zip_file()
    clean_up()

    # Return ZIP file path
    return "output/output_trace.zip"

if __name__ == "__main__":
    with open("sample/ai_system_analyzer_response.json", "r") as file:
        ai_system_input = json.load(file)

    with open("sample/pq_analyzer_response.json", "r") as file:
        pq_data_input = json.load(file)

    with open("sample/frontend_unification_response.json", "r") as file:
        unification_data_response = json.load(file)

    trace_templates = trace_template_generation.generate_trace_templates(unification_data_response,ai_system_input,pq_data_input)
    sparql_queries = sparql_query_generation.generate_sparql_queries(trace_templates, unification_data_response, ai_system_input, pq_data_input)

    build_output(trace_templates, sparql_queries)