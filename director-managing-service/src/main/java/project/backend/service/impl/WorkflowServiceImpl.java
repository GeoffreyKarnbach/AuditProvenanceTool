package project.backend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import project.backend.service.ExampleService;
import project.backend.service.WorkflowService;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkflowServiceImpl implements WorkflowService {

    private final RestTemplate restTemplate;

    private static final String AiSystemAnalyzerUrl = "http://localhost:5500/api/v1/workflow-mapping";

    private static final String PQAnalyzerUrl = "http://localhost:5501/api/v1/pq-analyzer";

    @Override
    public String initiateWorkflow(MultipartFile ttlFile, MultipartFile txtFile) {

        log.info("Initiating workflow with files: {} and {}", ttlFile.getOriginalFilename(), txtFile.getOriginalFilename());
        String ttlFileResponse = sendFileToAiSystemAnalyzer(ttlFile);
        String txtFileResponse = sendFileToPQAnalyzer(txtFile);

        log.info("Received responses: TTL - {}, TXT - {}", ttlFileResponse, txtFileResponse);

        return java.util.UUID.randomUUID().toString();
    }

    private String sendFileToAiSystemAnalyzer(MultipartFile file) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", file.getResource());

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(AiSystemAnalyzerUrl, requestEntity, String.class);

            log.info("Response from AI System Analyzer: {}", response.getBody());

            return response.getBody();
        } catch (Exception e) {
            log.error("Error sending file to {}", AiSystemAnalyzerUrl, e);
            throw new RuntimeException("Failed to send file", e);
        }
    }

    private String sendFileToPQAnalyzer(MultipartFile file) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", file.getResource());

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(PQAnalyzerUrl, requestEntity, String.class);

            log.info("Response from PQ Analyzer: {}", response.getBody());

            return response.getBody();
        } catch (Exception e) {
            log.error("Error sending file to {}", PQAnalyzerUrl, e);
            throw new RuntimeException("Failed to send file", e);
        }
    }
}
