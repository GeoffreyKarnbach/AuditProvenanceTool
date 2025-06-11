package project.backend.service;

import org.springframework.web.multipart.MultipartFile;
import project.backend.dto.UnificationClarificationManagingRequestDTO;

import java.io.IOException;

public interface WorkflowService {

    String initiateWorkflow(MultipartFile ttlFile, MultipartFile txtFile) throws IOException;

    Boolean ttlAnalyzeComplete(String processId);

    Boolean txtAnalyzeComplete(String processId);

    Boolean unificationFirstStepComplete(String processId);

    Boolean triggerUnificationWorkflow(String processId);

    UnificationClarificationManagingRequestDTO getUnificationFirstStepResponse(String processId);
}
