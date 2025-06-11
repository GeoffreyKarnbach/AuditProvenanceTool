package project.backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import project.backend.dto.PQAnalyzerResponseItemDTO;
import project.backend.dto.UnificationClarificationResponseDTO;
import project.backend.service.DelegationService;
import project.backend.workflowmapping.Workflow;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class DelegationServiceImpl implements DelegationService {

    private final RestTemplate restTemplate;

    private String AiSystemAnalyzerUrl;
    private String PQAnalyzerUrl;
    private String UnificationFirstStepUrl;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        String mode = System.getenv("mode");
        if (mode == null) {
            mode = "dev";
        }

        if (Objects.equals(mode, "prod")) {
            this.AiSystemAnalyzerUrl = "http://ai-system-analyzer-service:5500/api/v1/workflow-mapping";
            this.PQAnalyzerUrl = "http://pq-analyzer-service:5501/api/v1/pq-analyzer";
            this.UnificationFirstStepUrl = "http://unification-generation-service:5504/api/v1/unification-first-step";
        } else if (Objects.equals(mode, "dev")) {
            this.AiSystemAnalyzerUrl = "http://localhost:5500/api/v1/workflow-mapping";
            this.PQAnalyzerUrl = "http://localhost:5501/api/v1/pq-analyzer";
            this.UnificationFirstStepUrl = "http://localhost:5504/api/v1/unification-first-step";
        }

        log.info("WorkflowService initialized with URLs: {} and {}", AiSystemAnalyzerUrl, PQAnalyzerUrl);
    }

    @Override
    @Async
    public CompletableFuture<Workflow> sendFileToAiSystemAnalyzer(byte[] fileContent, String filename) {
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

            ResponseEntity<Workflow> response = restTemplate.postForEntity(AiSystemAnalyzerUrl, requestEntity, Workflow.class);

            return CompletableFuture.completedFuture(response.getBody());
        } catch (Exception e) {
            log.error("Error sending file to {}", AiSystemAnalyzerUrl, e);
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    @Async
    public CompletableFuture<List<PQAnalyzerResponseItemDTO>> sendFileToPQAnalyzer(byte[] fileContent, String filename) {
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

            ResponseEntity<List<PQAnalyzerResponseItemDTO>> response = restTemplate.exchange(
                PQAnalyzerUrl,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<List<PQAnalyzerResponseItemDTO>>() {}
            );
            return CompletableFuture.completedFuture(response.getBody());
        } catch (Exception e) {
            log.error("Error sending file to {}", PQAnalyzerUrl, e);
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    public CompletableFuture<UnificationClarificationResponseDTO> sendFilesToUnificationFirstStep(
        Workflow workflow,
        List<PQAnalyzerResponseItemDTO> pqAnalyzerResponse
    ) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            byte[] workflowJsonBytes = mapper.writeValueAsBytes(workflow);
            byte[] pqResponseJsonBytes = mapper.writeValueAsBytes(pqAnalyzerResponse);

            MultiValueMap<String, Object> body = getStringObjectMultiValueMap(workflowJsonBytes, pqResponseJsonBytes);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<UnificationClarificationResponseDTO> response = restTemplate.exchange(
                UnificationFirstStepUrl,
                HttpMethod.POST,
                requestEntity,
                UnificationClarificationResponseDTO.class
            );

            return CompletableFuture.completedFuture(response.getBody());

        } catch (Exception e) {
            log.error("Error sending files to {}", UnificationFirstStepUrl, e);
            return CompletableFuture.failedFuture(e);
        }
    }

    private static MultiValueMap<String, Object> getStringObjectMultiValueMap(byte[] workflowJsonBytes, byte[] pqResponseJsonBytes) {
        ByteArrayResource aiSystemAnalyzerFile = new ByteArrayResource(workflowJsonBytes) {
            @Override
            public String getFilename() {
                return "ai_system_analyzer.json";
            }
        };

        ByteArrayResource pqAnalyzerFile = new ByteArrayResource(pqResponseJsonBytes) {
            @Override
            public String getFilename() {
                return "pq_analyzer.json";
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("ai_system_analyzer", aiSystemAnalyzerFile);
        body.add("pq_analyzer", pqAnalyzerFile);
        return body;
    }
}

