package project.backend.endpoint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import project.backend.service.ExampleService;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping(value = "/api/v1/workflow")
@Slf4j
@RequiredArgsConstructor
public class WorkflowEndpoint {

    private final ExampleService exampleService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadFiles(
        @RequestPart("ttlFile") MultipartFile ttlFile,
        @RequestPart("txtFile") MultipartFile txtFile) {

        String ttlFileName = ttlFile.getOriginalFilename();
        String txtFileName = txtFile.getOriginalFilename();

        System.out.println("Received TTL file: " + ttlFileName);
        System.out.println("Received TXT file: " + txtFileName);

    }
}
