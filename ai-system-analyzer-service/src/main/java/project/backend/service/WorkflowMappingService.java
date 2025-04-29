package project.backend.service;

import project.backend.workflowmapping.Workflow;

import java.io.InputStream;

public interface WorkflowMappingService {

    /**
     * Maps the TTL file to a Workflow object.
     * @return The mapped Workflow object.
     */
    Workflow mapTTLToWorkflow(InputStream inputStream);
}
