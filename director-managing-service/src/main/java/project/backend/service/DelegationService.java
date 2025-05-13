package project.backend.service;

import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.CompletableFuture;

public interface DelegationService {

    @Async
    CompletableFuture<String> sendFileToAiSystemAnalyzer(byte[] fileContent, String filename);

    @Async
    CompletableFuture<String> sendFileToPQAnalyzer(byte[] fileContent, String filename);
}
