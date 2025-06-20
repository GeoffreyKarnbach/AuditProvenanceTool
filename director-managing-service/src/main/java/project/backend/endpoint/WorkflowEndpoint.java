package project.backend.endpoint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import project.backend.dto.UnificationClarificationFrontendResponseDTO;
import project.backend.dto.UnificationClarificationManagingRequestDTO;
import project.backend.service.WorkflowService;


@RestController
@RequestMapping(value = "/api/v1/workflow")
@Slf4j
@RequiredArgsConstructor
public class WorkflowEndpoint {

    private final WorkflowService workflowService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadFiles(
        @RequestPart("ttlFile") MultipartFile ttlFile,
        @RequestPart("txtFile") MultipartFile txtFile)
    {
        log.info("POST /api/v1/workflow");
        log.info("Received files: TTL - {}, TXT - {}", ttlFile.getOriginalFilename(), txtFile.getOriginalFilename());

        try {
            return workflowService.initiateWorkflow(ttlFile, txtFile);
        } catch (Exception e) {
            log.error("Error processing files", e);
            throw new RuntimeException("Error processing files", e);
        }
    }

    @GetMapping(value = "/ttl-analyze-complete/{processId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Boolean ttlAnalyzeComplete(@PathVariable String processId) {
        log.info("GET /api/v1/workflow/ttl-analyze-complete/{}", processId);
        return workflowService.ttlAnalyzeComplete(processId);
    }

    @GetMapping(value = "/txt-analyze-complete/{processId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Boolean txtAnalyzeComplete(@PathVariable String processId) {
        log.info("GET /api/v1/workflow/txt-analyze-complete/{}", processId);
        return workflowService.txtAnalyzeComplete(processId);
    }

    @GetMapping(value = "/unification-first-step-complete/{processId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Boolean unificationFirstStepComplete(@PathVariable String processId) {
        log.info("GET /api/v1/workflow/unification-first-step-complete/{}", processId);
        return workflowService.unificationFirstStepComplete(processId);
    }

    @PostMapping(value = "/trigger-unification-workflow/{processId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Boolean triggerUnificationWorkflow(@PathVariable String processId) {
        log.info("POST /api/v1/workflow/trigger-unification-workflow/{}", processId);
        return workflowService.triggerUnificationWorkflow(processId);
    }

    @GetMapping(value="/unification-first-step-response/{processId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UnificationClarificationManagingRequestDTO getUnificationFirstStepResponse(@PathVariable String processId) {
        log.info("GET /api/v1/workflow/unification-first-step-response/{}", processId);
        UnificationClarificationManagingRequestDTO response = this.workflowService.getUnificationFirstStepResponse(processId);
        if (response == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unification first step response not found for process ID: " + processId);
        }
        return response;
    }


    @PostMapping(value="/trigger-unification-second-step/{processId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Boolean triggerSecondStepUnificationWorkflow(@PathVariable String processId, @RequestBody UnificationClarificationFrontendResponseDTO unificationClarificationFrontendResponse) {
        log.info("POST /api/v1/workflow/trigger-unification-second-step/{}", processId);
        log.info("Received unification clarification response: {}", unificationClarificationFrontendResponse);
        return false;
    }
}
