package project.backend.endpoint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import project.backend.service.ExampleService;
import org.springframework.web.multipart.MultipartFile;
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
        @RequestPart("txtFile") MultipartFile txtFile) {

        String ttlFileName = ttlFile.getOriginalFilename();
        String txtFileName = txtFile.getOriginalFilename();

        System.out.println("Received TTL file: " + ttlFileName);
        System.out.println("Received TXT file: " + txtFileName);

        return workflowService.initiateWorkflow(ttlFile, txtFile);

    }
}
