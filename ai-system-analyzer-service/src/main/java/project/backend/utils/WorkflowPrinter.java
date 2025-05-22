package project.backend.utils;

import project.backend.workflowmapping.Workflow;
import project.backend.workflowmapping.WorkflowStep;

public class WorkflowPrinter {

    /**
     * Prints the workflow steps to the console.
     * @param workflow The Workflow object containing the steps to be printed.
     */
    public static void printWorkflow(Workflow workflow) {

        for (WorkflowStep step : workflow.getWorkflowSteps()) {
            System.out.println("Step Name: " + step.getStepName());
        }
    }
}
