package project.backend.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import project.backend.workflowmapping.Workflow;

@Component
@Slf4j
@RequiredArgsConstructor
public class WorkflowDefaultTraceAdder {

    /**
     * Adds default traces to the workflow for steps, agents, and variables.
     * @param workflow The workflow to which default traces will be added.
     */
    public void addDefaultTracesToWorkflow(Workflow workflow) {

    }
}
