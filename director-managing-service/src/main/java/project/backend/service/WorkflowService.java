package project.backend.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface WorkflowService {

    String initiateWorkflow(MultipartFile ttlFile, MultipartFile txtFile) throws IOException;

    Boolean ttlAnalyzeComplete(String processId);

    Boolean txtAnalyzeComplete(String processId);

    void triggerUnificationWorkflow(String processId);
}
