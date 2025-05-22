package project.backend.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import project.backend.workflowmapping.Workflow;
import project.backend.workflowmapping.WorkflowAgent;
import project.backend.workflowmapping.WorkflowStep;
import project.backend.workflowmapping.WorkflowVariable;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class WorkflowDefaultTraceAdder {

    Map<String, List<String>> stepConfig;
    Map<String, List<String>> agentConfig;
    Map<String, List<String>> variableConfig;

    /**
     * Adds default traces to the workflow for steps, agents, and variables.
     * @param workflow The workflow to which default traces will be added.
     */
    public void addDefaultTracesToWorkflow(Workflow workflow) {
        this.stepConfig = loadCorrespondingJSONConfig("stepTypes.json");
        this.agentConfig = loadCorrespondingJSONConfig("agentTypes.json");
        this.variableConfig = loadCorrespondingJSONConfig("variableTypes.json");

        for (WorkflowStep step : workflow.getWorkflowSteps()) {
            annotateWorkflowStep(step);
        }

        for (WorkflowVariable variable : workflow.getWorkflowVariables()) {
            annotateWorkflowVariable(variable);
        }

        for (WorkflowAgent agent : workflow.getWorkflowAgents()) {
            annotateWorkflowAgent(agent);
        }
    }

    /**
     * Annotates a workflow step with default traces based on its type.
     * Also annotates the agents and variables associated with the step.
     * @param step The workflow step to annotate.
     */
    private void annotateWorkflowStep(WorkflowStep step) {
        step.getDefaultTraceFields().addAll(getAnnotationsForType(step.getStepSubtype(), stepConfig));
        step.getDefaultTraceFields().addAll(getAnnotationsForType("default", stepConfig));
    }

    /**
     * Annotates a workflow variable with default traces based on its type.
     * @param variable The workflow variable to annotate.
     */
    private void annotateWorkflowVariable(WorkflowVariable variable) {
        variable.getDefaultTraceFields().addAll(getAnnotationsForType(variable.getVariableSubtype(), variableConfig));
        variable.getDefaultTraceFields().addAll(getAnnotationsForType("default", variableConfig));
    }

    /**
     * Annotates a workflow agent with default traces based on its type.
     * @param agent The workflow agent to annotate.
     */
    private void annotateWorkflowAgent(WorkflowAgent agent) {
        agent.getDefaultTraceFields().addAll(getAnnotationsForType("default", agentConfig));
    }

    private List<String> getAnnotationsForType(String type, Map<String, List<String>> config) {
        List<String> defaultTraceFields = config.get(type);
        if (defaultTraceFields == null) {
            log.warn("No default trace fields found for type: {}", type);
            return List.of();
        }
        return defaultTraceFields;
    }


    private Map<String, List<String>> loadCorrespondingJSONConfig(String fileName) {
        ObjectMapper objectMapper = new ObjectMapper();
        String path = "traceFields/" + fileName;

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("File not found: " + path);
            }
            return objectMapper.readValue(inputStream, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException("Failed to load or parse JSON config from: " + path, e);
        }
    }
}
