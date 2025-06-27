package project.backend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import project.backend.dto.PQAnalyzerResponseItemDTO;
import project.backend.dto.UnificationClarificationFrontendResponseDTO;
import project.backend.dto.UnificationClarificationManagingRequestDTO;
import project.backend.dto.UnificationClarificationResponseDTO;
import project.backend.service.DelegationService;
import project.backend.service.WorkflowService;
import project.backend.workflowmapping.Workflow;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkflowServiceImpl implements WorkflowService {

    private final DelegationService delegationService;
    private final HashMap<String, Workflow> processIdAiSystemAnalyzerResponse = new HashMap<>();
    private final HashMap<String, List<PQAnalyzerResponseItemDTO>> processIdPQAnalyzerResponse = new HashMap<>();
    private final HashMap<String, UnificationClarificationResponseDTO> processUnificationFirstStepResponse = new HashMap<>();
    private final HashMap<String, byte[]> processUnificationSecondStepResponse = new HashMap<>();

    @Override
    public String initiateWorkflow(MultipartFile ttlFile, MultipartFile txtFile) throws IOException {
        String processId = java.util.UUID.randomUUID().toString();
        log.info("Generated process ID: {}", processId);

        byte[] ttlBytes = ttlFile.getBytes();
        String ttlFilename = ttlFile.getOriginalFilename();

        byte[] txtBytes = txtFile.getBytes();
        String txtFilename = txtFile.getOriginalFilename();

        this.delegationService.sendFileToAiSystemAnalyzer(ttlBytes, ttlFilename)
            .thenAccept(response -> {
                log.info("Response from AI System Analyzer: {}", response);
                processIdAiSystemAnalyzerResponse.put(processId, response);
            })
            .exceptionally(e -> {
                log.error("Error sending file to AI System Analyzer", e);
                return null;
            });

        this.delegationService.sendFileToPQAnalyzer(txtBytes, txtFilename)
            .thenAccept(response -> {
                log.info("Response from PQ Analyzer: {}", response);
                processIdPQAnalyzerResponse.put(processId, response);
            })
            .exceptionally(e -> {
                log.error("Error sending file to PQ Analyzer", e);
                return null;
            });

        return processId;
    }

    @Override
    public Boolean ttlAnalyzeComplete(String processId) {
        return processIdAiSystemAnalyzerResponse.containsKey(processId);
    }

    @Override
    public Boolean txtAnalyzeComplete(String processId) {
        return processIdPQAnalyzerResponse.containsKey(processId);
    }

    @Override
    public Boolean unificationFirstStepComplete(String processId) {
        return processUnificationFirstStepResponse.containsKey(processId);
    }

    @Override
    public Boolean triggerUnificationWorkflow(String processId) {
        log.info("Triggering unification workflow for process ID: {}", processId);
        Workflow aiSystemAnalyzerResponse = processIdAiSystemAnalyzerResponse.get(processId);
        List<PQAnalyzerResponseItemDTO> pqAnalyzerResponse = processIdPQAnalyzerResponse.get(processId);

        if (aiSystemAnalyzerResponse != null && pqAnalyzerResponse != null) {
            this.delegationService.sendFilesToUnificationFirstStep(processId, aiSystemAnalyzerResponse, pqAnalyzerResponse)
                .thenAccept(response -> {
                    log.info("Unification first step response: {}", response);
                    processUnificationFirstStepResponse.put(processId, response);
                })
                .exceptionally(e -> {
                    log.error("Error triggering unification workflow", e);
                    return null;
                });
            return true;
        } else {
            log.warn("Unification workflow cannot be triggered. Missing responses for process ID: {}", processId);
            return false;
        }
    }

    @Override
    public UnificationClarificationManagingRequestDTO getUnificationFirstStepResponse(String processId) {

        Workflow aiSystemAnalyzerResponse = processIdAiSystemAnalyzerResponse.get(processId);
        UnificationClarificationResponseDTO response = processUnificationFirstStepResponse.get(processId);

        if (aiSystemAnalyzerResponse == null || response == null) {
            log.warn("No unification first step response found for process ID: {}", processId);
            return null;
        }

        return UnificationClarificationManagingRequestDTO.builder()
            .unificationClarificationResponse(response)
            .workflow(aiSystemAnalyzerResponse)
            .build();
    }

    @Override
    public Boolean triggerUnificationWorkflowSecondStep(String processId, UnificationClarificationFrontendResponseDTO requestDTO) {
        this.delegationService.sendToUnificationSecondStep(processId, requestDTO)
            .thenAccept(success -> {
                log.info("Unification second step response received for process ID: {}", processId);
                processUnificationSecondStepResponse.put(processId, success);
            })
            .exceptionally(e -> {
                log.error("Error triggering unification second step", e);
                return null;
            });

        return true;
    }

    @Override
    public Boolean outputGenerationComplete(String processId) {
        return processUnificationSecondStepResponse.containsKey(processId);
    }

    @Override
    public byte[] getOutputGenerationResponse(String processId) {
        return processUnificationSecondStepResponse.get(processId);
    }
}
