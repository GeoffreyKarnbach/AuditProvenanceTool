package project.backend.service;

import org.springframework.web.multipart.MultipartFile;

public interface WorkflowService {

    String initiateWorkflow(MultipartFile ttlFile, MultipartFile txtFile);
}
