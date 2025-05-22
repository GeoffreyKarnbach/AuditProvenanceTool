package project.backend.service.impl;

import org.apache.jena.rdf.model.*;
import project.backend.service.WorkflowMappingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.backend.utils.TTLMapper;
import project.backend.utils.WorkflowDefaultTraceAdder;
import project.backend.utils.WorkflowPrinter;
import project.backend.workflowmapping.Workflow;

import java.io.InputStream;
import java.io.InputStreamReader;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkflowMappingServiceImpl implements WorkflowMappingService {

    private final TTLMapper ttlMapper;

    private final WorkflowDefaultTraceAdder workflowDefaultTraceAdder;

    @Override
    public Workflow mapTTLToWorkflow(InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }

        Model model = ModelFactory.createDefaultModel();
        try {
            InputStreamReader reader = new InputStreamReader(inputStream);
            model.read(reader, null, "TTL");
        } catch (Exception e) {
            throw new RuntimeException("Error reading TTL file", e);
        }

        // Map the TTL model to a Workflow object
        Workflow workflow = ttlMapper.mapTTLToWorkflow(model);

        // Add default traces to the workflow
        workflowDefaultTraceAdder.addDefaultTracesToWorkflow(workflow);

        // Print the workflow steps to the console
        WorkflowPrinter.printWorkflow(workflow);

        return workflow;
    }
}
