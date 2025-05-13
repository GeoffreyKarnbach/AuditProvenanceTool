package project.backend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import project.backend.service.DelegationService;
import project.backend.service.WorkflowService;

import java.io.IOException;
import java.util.HashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkflowServiceImpl implements WorkflowService {

    private final DelegationService delegationService;
    private final HashMap<String, String> processIdAiSystemAnalyzerResponse = new HashMap<>();
    private final HashMap<String, String> processIdPQAnalyzerResponse = new HashMap<>();

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
    public void triggerUnificationWorkflow(String processId) {
        log.info("Triggering unification workflow for process ID: {}", processId);
        String aiSystemAnalyzerResponse = processIdAiSystemAnalyzerResponse.get(processId);
        String pqAnalyzerResponse = processIdPQAnalyzerResponse.get(processId);

        if (aiSystemAnalyzerResponse != null && pqAnalyzerResponse != null) {
            log.info("Unification workflow triggered with responses: AI System Analyzer - {}, PQ Analyzer - {}",
                aiSystemAnalyzerResponse, pqAnalyzerResponse);
        } else {
            log.warn("Unification workflow cannot be triggered. Missing responses for process ID: {}", processId);
        }
    }
}
