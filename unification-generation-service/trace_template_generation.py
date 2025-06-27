number_trace_default_names = [
    "start_time",
    "end_time",
]

def generate_trace_templates(unification_response, ai_system_data, pq_data):
    result = {}

    for step in ai_system_data["workflowSteps"]:
        filename = "trace_template_" + step["id"].split("/")[-1] + ".json"
        trace_template = {item:"string" for item in step["defaultTraceFields"]}

        for i, field in enumerate(trace_template):
            if field == "agent":
                agent_subtrace = {}
                for agent in step["workflowAgents"]:
                    agent_subtrace[agent["id"]] = {}
                    for trace_field in agent["defaultTraceFields"]:
                        agent_subtrace[agent["id"]][trace_field] = "string" if trace_field not in number_trace_default_names else "number"

                trace_template["agent"] = agent_subtrace

            if field in number_trace_default_names:
                trace_template[field] = "number"

            if field == "inputs":
                input_subtrace = {}
                for inputVar in step["workflowInputVariables"]:
                    input_subtrace[inputVar["id"]] = {}
                    for trace_field in inputVar["defaultTraceFields"]:
                        input_subtrace[inputVar["id"]][trace_field] = "string" if trace_field not in number_trace_default_names else "number"

                trace_template["inputs"] = input_subtrace

            if field == "output":
                output_subtrace = {}
                for outputVar in step["workflowOutputVariables"]:
                    output_subtrace[outputVar["id"]] = {}
                    for trace_field in outputVar["defaultTraceFields"]:
                        output_subtrace[outputVar["id"]][trace_field] = "string" if trace_field not in number_trace_default_names else "number"

                trace_template["output"] = output_subtrace

        for item in unification_response["items"]:
            if item["selectedTraceValue"] is None or item["selectedTraceValue"] == "":
                continue

            if item["selectedActivityId"] == step["id"]:
                for inputVar in step["workflowInputVariables"]:
                    if inputVar["id"] == item["selectedEntityId"]:
                        trace_template["inputs"][inputVar["id"]][item["selectedTraceValue"]] = "string"

                for outputVar in step["workflowOutputVariables"]:
                    if outputVar["id"] == item["selectedEntityId"]:
                        trace_template["output"][outputVar["id"]][item["selectedTraceValue"]] = "string"

        result[filename] = trace_template

    return result