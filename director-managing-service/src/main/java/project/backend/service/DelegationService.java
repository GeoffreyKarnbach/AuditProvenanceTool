package project.backend.service;

import org.springframework.scheduling.annotation.Async;
import project.backend.dto.PQAnalyzerResponseItemDTO;
import project.backend.dto.UnificationClarificationFrontendResponseDTO;
import project.backend.dto.UnificationClarificationResponseDTO;
import project.backend.workflowmapping.Workflow;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface DelegationService {

    @Async
    CompletableFuture<Workflow> sendFileToAiSystemAnalyzer(byte[] fileContent, String filename);

    @Async
    CompletableFuture<List<PQAnalyzerResponseItemDTO>> sendFileToPQAnalyzer(byte[] fileContent, String filename);

    @Async
    CompletableFuture<UnificationClarificationResponseDTO> sendFilesToUnificationFirstStep(String processId, Workflow workflow, List<PQAnalyzerResponseItemDTO> pqAnalyzerResponse);

    @Async
    CompletableFuture<byte[]> sendToUnificationSecondStep(String processId, UnificationClarificationFrontendResponseDTO requestDTO);

}
