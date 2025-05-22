package project.backend.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.springframework.stereotype.Component;
import project.backend.workflowmapping.Workflow;
import project.backend.workflowmapping.WorkflowAgent;
import project.backend.workflowmapping.WorkflowStep;
import project.backend.workflowmapping.WorkflowVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class TTLMapper {

    /**
     * Maps the TTL model to a Workflow object by extracting agents, steps, and variables.
     * @param model The TTL model to be mapped.
     * @return The mapped Workflow object.
     */
    public Workflow mapTTLToWorkflow(Model model) {
        List<WorkflowAgent> agents = this.getAgents(model);

        Map<String, List<String>> stepsToAgentMap = new HashMap<>();

        for (WorkflowAgent agent : agents) {
            List<String> steps = this.getActorSteps(model, agent.getId());

            for (String step : steps) {
                if (!stepsToAgentMap.containsKey(step)) {
                    stepsToAgentMap.put(step, new ArrayList<>());
                }
                stepsToAgentMap.get(step).add(agent.getId());
            }
        }

        List<WorkflowVariable> variables = this.getWorkflowVariables(model);

        List<WorkflowStep> steps = this.getSteps(model, variables, agents, stepsToAgentMap);
        updateOrderInformation(steps);

        return Workflow.builder()
            .workflowSteps(steps)
            .workflowAgents(agents)
            .workflowVariables(variables)
            .build();
    }

    /**
     * Retrieves the steps from the model.
     * @param model The TTL model to be processed.
     * @return A list of WorkflowStep objects representing the steps in the model.
     */
    private List<WorkflowStep> getSteps(
        Model model,
        List<WorkflowVariable> variables,
        List<WorkflowAgent> agents,
        Map<String, List<String>> stepsToAgentMap
    ) {
        String PPLAN_STEP = "http://purl.org/net/p-plan#Step";
        Resource stepClass = model.getResource(PPLAN_STEP);
        List<WorkflowStep> steps = new ArrayList<>();

        for (Resource stepRes : model.listResourcesWithProperty(RDF.type, stepClass).toList()) {
            // Step ID
            String uri = stepRes.getURI();

            // Step name
            Statement labelStmt = stepRes.getProperty(RDFS.label);
            String name = (labelStmt != null) ? labelStmt.getObject().toString() : uri;

            // Step type
            String stepType = null;
            StmtIterator iter = stepRes.listProperties(RDF.type);
            while (iter.hasNext()) {
                Statement stmt = iter.nextStatement();
                String typeUri = stmt.getObject().toString();
                if (typeUri.startsWith("https://w3id.org/audit/ns#")) {
                    stepType = typeUri;
                    break;
                }
            }

            // Previous step
            String previousStepId = null;
            StmtIterator iter2 = stepRes.listProperties(model.getProperty("http://purl.org/net/p-plan#isPrecededBy"));
            if (iter2.hasNext()) {
                Statement stmt = iter2.nextStatement();
                previousStepId = stmt.getObject().toString();
            }

            // Step Input Variables
            List<WorkflowVariable> inputVariables = new ArrayList<>();
            StmtIterator iter3 = stepRes.listProperties(model.getProperty("http://purl.org/net/p-plan#hasInputVar"));
            while (iter3.hasNext()) {
                Statement stmt = iter3.nextStatement();
                String varUri = stmt.getObject().toString();
                for (WorkflowVariable variable : variables) {
                    if (variable.getId().equals(varUri)) {
                        inputVariables.add(variable);
                    }
                }
            }

            // Step Output Variables
            List<WorkflowVariable> outputVariables = new ArrayList<>();
            StmtIterator iter4 = stepRes.listProperties(model.getProperty("http://purl.org/net/p-plan#hasOutputVar"));
            while (iter4.hasNext()) {
                Statement stmt = iter4.nextStatement();
                String varUri = stmt.getObject().toString();
                for (WorkflowVariable variable : variables) {
                    if (variable.getId().equals(varUri)) {
                        outputVariables.add(variable);
                    }
                }
            }

            // Step Agents
            List<WorkflowAgent> stepAgents = new ArrayList<>();
            if (stepsToAgentMap.containsKey(uri)) {
                for (String agentUri : stepsToAgentMap.get(uri)) {
                    for (WorkflowAgent agent : agents) {
                        if (agent.getId().equals(agentUri)) {
                            stepAgents.add(agent);
                        }
                    }
                }
            }

            WorkflowStep step = WorkflowStep.builder()
                .id(uri)
                .stepName(name)
                .previousStepId(previousStepId)
                .workflowAgents(stepAgents)
                .workflowInputVariables(inputVariables)
                .workflowOutputVariables(outputVariables)
                .stepSubtype(stepType)
                .defaultTraceFields(new ArrayList<>())
                .build();

            steps.add(step);
        }

        return steps;
    }

    /**
     * Retrieves the agents from the model.
     * @param model The TTL model to be processed.
     * @return A list of WorkflowAgent objects representing the agents in the model.
     */
    private List<WorkflowAgent> getAgents(Model model ) {
        String AUDIT_ACTOR = "https://w3id.org/audit/ns#Actor";
        Resource actorClass = model.getResource(AUDIT_ACTOR);
        List<WorkflowAgent> agents = new ArrayList<>();

        for (Resource agentRes : model.listResourcesWithProperty(RDF.type, actorClass).toList()) {
            String uri = agentRes.getURI();
            Statement labelStmt = agentRes.getProperty(RDFS.label);
            String name = (labelStmt != null) ? labelStmt.getObject().toString() : uri;

            WorkflowAgent agent = WorkflowAgent.builder()
                .id(uri)
                .agentName(name)
                .defaultTraceFields(new ArrayList<>())
                .build();

            agents.add(agent);
        }
        return agents;
    }

    /**
     * Retrieves the steps associated with a specific actor.
     * @param model The TTL model to be processed.
     * @param actorUri The URI of the actor whose steps are to be retrieved.
     * @return A list of strings representing the steps associated with the actor.
     */
    private List<String> getActorSteps(Model model, String actorUri) {
        String AUDIT_ACTOR = "https://w3id.org/audit/ns#Actor";
        String AUDIT_OPERATE = "https://w3id.org/audit/ns#operate";

        Resource actorClass = model.getResource(AUDIT_ACTOR);
        Property operateProperty = model.getProperty(AUDIT_OPERATE);
        List<String> steps = new ArrayList<>();
        for (Resource agentRes : model.listResourcesWithProperty(RDF.type, actorClass).toList()) {
            if (agentRes.getURI().equals(actorUri)) {
                StmtIterator iter = agentRes.listProperties(operateProperty);
                while (iter.hasNext()) {
                    Statement stmt = iter.nextStatement();
                    steps.add(stmt.getObject().toString());
                }
            }
        }

        return steps;
    }

    /**
     * Retrieves the workflow variables from the model.
     * @param model The TTL model to be processed.
     * @return A list of WorkflowVariable objects representing the variables in the model.
     */
    private List<WorkflowVariable> getWorkflowVariables(Model model) {
        String PPLAN_VARIABLE = "http://purl.org/net/p-plan#Variable";
        Resource variableClass = model.getResource(PPLAN_VARIABLE);
        List<WorkflowVariable> variables = new ArrayList<>();

        for (Resource variableRes : model.listResourcesWithProperty(RDF.type, variableClass).toList()) {
            String uri = variableRes.getURI();
            Statement labelStmt = variableRes.getProperty(RDFS.label);
            String name = (labelStmt != null) ? labelStmt.getObject().toString() : uri;

            String varType = null;
            StmtIterator iter = variableRes.listProperties(RDF.type);
            while (iter.hasNext()) {
                Statement stmt = iter.nextStatement();
                String typeUri = stmt.getObject().toString();
                if (typeUri.startsWith("https://w3id.org/audit/ns#")) {
                    varType = typeUri;
                    break;
                }
            }

            WorkflowVariable variable = WorkflowVariable.builder()
                .id(uri)
                .variableName(name)
                .variableSubtype(varType)
                .defaultTraceFields(new ArrayList<>())
                .build();

            variables.add(variable);
        }
        return variables;
    }

    /**
     * Updates the order information of the workflow steps.
     * @param steps The list of WorkflowStep objects to be updated.
     */
    private void updateOrderInformation(List<WorkflowStep> steps) {
        // Find first step that has no previous step
        WorkflowStep firstStep = null;
        for (WorkflowStep step : steps) {
            if (step.getPreviousStepId() == null) {
                firstStep = step;
                break;
            }
        }

        // Update step numbers
        if (firstStep != null) {
            firstStep.setStepNumber(1L);
            updateStepNumbers(firstStep, steps, 2L);
        }

        // Sort steps by step number
        steps.sort((s1, s2) -> {
            if (s1.getStepNumber() == null && s2.getStepNumber() == null) {
                return 0;
            } else if (s1.getStepNumber() == null) {
                return 1;
            } else if (s2.getStepNumber() == null) {
                return -1;
            } else {
                return s1.getStepNumber().compareTo(s2.getStepNumber());
            }
        });

        // Update previous and next step IDs
        for (int i = 0; i < steps.size(); i++) {
            WorkflowStep step = steps.get(i);
            if (i > 0) {
                step.setPreviousStepId(steps.get(i - 1).getId());
            } else {
                step.setPreviousStepId(null);
            }
            if (i < steps.size() - 1) {
                step.setNextStepId(steps.get(i + 1).getId());
            } else {
                step.setNextStepId(null);
            }
        }
    }

    /**
     * Recursively updates the step numbers of the workflow steps.
     * @param step The current WorkflowStep object.
     * @param steps The list of WorkflowStep objects to be updated.
     * @param stepNumber The current step number to be assigned.
     */
    private void updateStepNumbers(WorkflowStep step, List<WorkflowStep> steps, Long stepNumber) {
        for (WorkflowStep nextStep : steps) {
            if (nextStep.getPreviousStepId() != null && nextStep.getPreviousStepId().equals(step.getId())) {
                nextStep.setStepNumber(stepNumber);
                updateStepNumbers(nextStep, steps, stepNumber + 1);
            }
        }
    }
}
