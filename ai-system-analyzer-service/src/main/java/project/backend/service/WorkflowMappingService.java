package project.backend.service;

import project.backend.workflowmapping.Workflow;

import java.io.InputStream;

public interface WorkflowMappingService {

    /**
     * Maps the TTL file to a Workflow object, adds default traces, and prints the workflow steps.
     * @return The mapped Workflow object.
     */
    Workflow mapTTLToWorkflow(InputStream inputStream);
}
