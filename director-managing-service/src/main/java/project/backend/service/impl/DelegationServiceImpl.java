package project.backend.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import project.backend.service.DelegationService;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class DelegationServiceImpl implements DelegationService {

    private final RestTemplate restTemplate;

    private String AiSystemAnalyzerUrl;
    private String PQAnalyzerUrl;

    @PostConstruct
    public void init() {
        String mode = System.getenv("mode");
        if (mode == null) {
            mode = "dev";
        }

        if (Objects.equals(mode, "prod")) {
            this.AiSystemAnalyzerUrl = "http://ai-system-analyzer-service:5500/api/v1/workflow-mapping";
            this.PQAnalyzerUrl = "http://pq-analyzer-service:5501/api/v1/pq-analyzer";
        } else if (Objects.equals(mode, "dev")) {
            this.AiSystemAnalyzerUrl = "http://localhost:5500/api/v1/workflow-mapping";
            this.PQAnalyzerUrl = "http://localhost:5501/api/v1/pq-analyzer";
        }

        log.info("WorkflowService initialized with URLs: {} and {}", AiSystemAnalyzerUrl, PQAnalyzerUrl);
    }

    @Override
    @Async
    public CompletableFuture<String> sendFileToAiSystemAnalyzer(byte[] fileContent, String filename) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(fileContent) {
                @Override
                public String getFilename() {
                    return filename;
                }
            });

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(AiSystemAnalyzerUrl, requestEntity, String.class);

            return CompletableFuture.completedFuture(response.getBody());
        } catch (Exception e) {
            log.error("Error sending file to {}", AiSystemAnalyzerUrl, e);
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    @Async
    public CompletableFuture<String> sendFileToPQAnalyzer(byte[] fileContent, String filename) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(fileContent) {
                @Override
                public String getFilename() {
                    return filename;
                }
            });

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(PQAnalyzerUrl, requestEntity, String.class);

            return CompletableFuture.completedFuture(response.getBody());
        } catch (Exception e) {
            log.error("Error sending file to {}", PQAnalyzerUrl, e);
            return CompletableFuture.failedFuture(e);
        }
    }
}

