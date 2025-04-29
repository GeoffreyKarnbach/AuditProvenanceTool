package project.backend.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.backend.service.WorkflowMappingService;
import project.backend.workflowmapping.Workflow;

import java.io.IOException;

@RestController
@RequestMapping(value = "/api/v1/workflow-mapping")
@Slf4j
@RequiredArgsConstructor
public class WorkflowMappingEndpoint {

    private final WorkflowMappingService workflowMappingService;

    @PostMapping(consumes = "multipart/form-data")
    @Operation(summary = "Upload TTL File and Get Workflow Mapping", description = "Upload a TTL file and receive the mapped Workflow object")
    @ResponseStatus(HttpStatus.OK)
    public Workflow uploadTTLAndMapWorkflow(@RequestParam("file") MultipartFile file) throws IOException {
        log.info("POST /api/v1/workflow-mapping - Received file: {}", file.getOriginalFilename());
        return workflowMappingService.mapTTLToWorkflow(file.getInputStream());
    }
}
