package project.backend.service.impl;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;
import project.backend.service.WorkflowMappingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.backend.workflowmapping.Workflow;
import project.backend.workflowmapping.WorkflowStep;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkflowMappingServiceImpl implements WorkflowMappingService {

    @Override
    public Workflow mapTTLToWorkflow(InputStream inputStream) {
        Model model = ModelFactory.createDefaultModel();
        try {
            if (inputStream == null) {
                throw new IllegalArgumentException("InputStream cannot be null");
            }

            InputStreamReader reader = new InputStreamReader(inputStream);
            model.read(reader, null, "TTL");

            Workflow workflow = mapTTLToWorkflow(model);
            printWorkflow(workflow);

            return workflow;
        } catch (Exception e) {
            throw new RuntimeException("Error reading TTL file", e);
        }
    }

    /**
     * Maps the TTL model to a Workflow object.
     * @param model The TTL model to be mapped.
     * @return The mapped Workflow object.
     */
    private Workflow mapTTLToWorkflow(Model model) {

        String PPLAN_NS = "http://purl.org/net/p-plan#";
        String RDFS_NS = "http://www.w3.org/2000/01/rdf-schema#";

        Property labelProp = model.getProperty(RDFS_NS + "label");
        Property precededByProp = model.getProperty(PPLAN_NS + "wasPrecededBy");
        Resource stepClass = model.getResource(PPLAN_NS + "Step");

        Map<String, WorkflowStep> stepMap = new HashMap<>();

        ResIterator stepIterator = model.listResourcesWithProperty(RDF.type, stepClass);
        while (stepIterator.hasNext()) {
            Resource stepRes = stepIterator.next();
            String uri = stepRes.getURI();

            Statement labelStmt = stepRes.getProperty(labelProp);
            String name = (labelStmt != null) ? labelStmt.getObject().toString() : uri;

            WorkflowStep step = WorkflowStep.builder()
                .stepName(name)
                .build();

            stepMap.put(uri, step);
        }

        for (Map.Entry<String, WorkflowStep> entry : stepMap.entrySet()) {
            Resource stepRes = model.getResource(entry.getKey());
            WorkflowStep step = entry.getValue();

            Statement prevStmt = stepRes.getProperty(precededByProp);
            if (prevStmt != null && prevStmt.getObject().isResource()) {
                Resource prevRes = prevStmt.getResource();
                WorkflowStep prevStep = stepMap.get(prevRes.getURI());
                step.setPreviousStep(prevStep);
                if (prevStep != null) {
                    prevStep.setNextStep(step);
                }
            }
        }

        List<WorkflowStep> orderedSteps = new ArrayList<>();
        stepMap.values().stream()
            .filter(s -> s.getPreviousStep() == null)
            .findFirst()
            .ifPresent(first -> {
                int count = 1;
                WorkflowStep current = first;
                while (current != null) {
                    current.setStepNumber((long) count++);
                    orderedSteps.add(current);
                    current = current.getNextStep();
                }
            });

        return Workflow.builder()
            .workflowSteps(orderedSteps)
            .build();
    }

    /**
     * Prints the workflow steps to the console.
     * @param workflow The Workflow object containing the steps to be printed.
     */
    private void printWorkflow(Workflow workflow) {
        for (WorkflowStep step : workflow.getWorkflowSteps()) {
            System.out.println("Step Name: " + step.getStepName());
            System.out.println("Step Number: " + step.getStepNumber());
            if (step.getPreviousStep() != null) {
                System.out.println("Previous Step: " + step.getPreviousStep().getStepName());
            }
            if (step.getNextStep() != null) {
                System.out.println("Next Step: " + step.getNextStep().getStepName());
            }
            System.out.println();
        }
    }
}
