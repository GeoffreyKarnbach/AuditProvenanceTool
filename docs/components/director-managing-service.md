# Director Managing Service

The **Director Managing Service** is the central nervous system of the entire application. It acts as both an API Gateway for the frontend and a sophisticated orchestrator for the backend microservices. Its primary responsibility is to manage the entire end-to-end workflow, from initial file upload to final artifact generation.

- **Technology:** Java (Spring Boot)
- **Port:** `5502`

## Key Responsibilities

- **Central API Gateway:** It is the single point of contact for the frontend, simplifying the client-side logic by abstracting the distributed nature of the backend.
- **Asynchronous Workflow Orchestration:** It manages a complex, multi-step process involving several services without blocking. It uses Java's `CompletableFuture` to handle asynchronous operations efficiently.
- **State Management:** It tracks the state of each user request (workflow) from start to finish using a unique process ID.
- **Service Delegation:** It delegates specific tasks (e.g., TTL parsing, NLP analysis) to the appropriate microservice.

---

## For Developers (Living Documentation)

This section provides technical details for developers working on the Director Managing Service.

### Development & Build Setup


#### Local Development
There are two primary ways to run the service locally for development or debugging, with the only supported and tested way being:

**Using an IDE (Recommended):**
The simplest way to run the service is directly from your IDE.
- Locate the main application class (the one annotated with `@SpringBootApplication`).
- Run its `public static void main(String[] args)` method. In IntelliJ IDEA, you can do this by clicking the green "Run" arrow next to the method or the class definition.


#### Docker Build Process

The service is containerized using a multi-stage `Dockerfile` to create an optimized, lightweight, and secure production image. This approach separates the build environment from the production environment.

1.  **Build Stage (`build`):**

    - Uses a `maven:3.9-eclipse-temurin-17` image, which contains all the necessary tools (Maven, JDK 17) to build the application.
    - **Dependency Caching:** It first copies only the `pom.xml` and runs `mvn dependency:go-offline`. This downloads all dependencies into a separate Docker layer. This layer is cached and only re-executed if the `pom.xml` file changes, dramatically speeding up subsequent builds when only source code has been modified.
    - **Compilation:** The source code is then copied, and `mvn clean package` compiles the code and packages it into a single executable `app.jar` file.

2.  **Production Stage (`production-stage`):**
    - Starts from a minimal `eclipse-temurin:17-jdk-alpine` base image. The `alpine` version is significantly smaller than the full build image, reducing the final image size and attack surface.
    - **Artifact Copy:** It copies _only_ the `app.jar` file from the `build` stage. None of the build tools, source code, or intermediate files are included in the final image.
    - **Execution:** The `ENTRYPOINT` is set to run the application using `java -jar /app/app.jar`.

<details>
<summary><b>Click to see the full Dockerfile</b></summary>

```dockerfile
# Stage 1: Build the Java application using Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY checkstyle.xml .

# Download dependencies first to leverage Docker layer caching
RUN mvn dependency:go-offline -DskipTests

# Copy source and build the application
COPY src ./src
RUN mvn clean package -DskipTests -Dcheckstyle.skip=true

# Stage 2: Create the lightweight production image
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
# Copy only the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar
# Set the command to run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

</details>

### Asynchronous Polling Workflow

Instead of making the frontend wait for a long-running request, the Director implements a resilient polling mechanism:

1.  **Initiation:** The frontend `POST`s the initial `.ttl` and `.txt` files.
2.  **Process ID Return:** The Director immediately generates a unique `processId` and returns it. It then asynchronously sends the files to the `AI System Analyzer` and `PQ Analyzer` services in parallel.
3.  **Frontend Polling:** The frontend uses the `processId` to periodically call status endpoints (e.g., `/ttl-analyze-complete/{processId}`) to check progress.
4.  **Triggering Next Step:** Once tasks are complete, the frontend sends a request to trigger the next phase of the workflow.
5.  **Final Retrieval:** Once output generation is complete, the frontend calls the final endpoint to download the resulting ZIP archive.

### State Management: In-Memory Storage

The Director manages the state of all ongoing workflows entirely in-memory using `HashMap`s keyed by the `processId`.

```java
// Snippet from WorkflowServiceImpl.java
private final HashMap<String, Workflow> processIdAiSystemAnalyzerResponse = new HashMap<>();
private final HashMap<String, List<PQAnalyzerResponseItemDTO>> processIdPQAnalyzerResponse = new HashMap<>();
private final HashMap<String, UnificationClarificationResponseDTO> processUnificationFirstStepResponse = new HashMap<>();
private final HashMap<String, byte[]> processUnificationSecondStepResponse = new HashMap<>();
```

!> **Stateful, Single-Instance Design:** This in-memory approach has a critical implication: the service is **stateful**. If the Director service restarts or crashes, the state of all in-progress workflows is **lost**. This design was chosen for simplicity and assumes the service is deployed as a single, non-replicated instance. For future scalability, this could be replaced with an external cache like Redis.

### Configuration & Service Discovery

The Director needs to know the network addresses of the other microservices. This is configured dynamically based on the `mode` environment variable.

- If `mode=prod` (the default in `docker-compose.yml`), it uses Docker network service names (e.g., `http://ai-system-analyzer-service:5500`).
- If `mode=dev` (or is not set), it defaults to `localhost` URLs.

```java
// Snippet from DelegationServiceImpl.java
@PostConstruct
public void init() {
    String mode = System.getenv("mode");
    if (mode == null) {
        mode = "dev";
    }

    if (Objects.equals(mode, "prod")) {
        this.AiSystemAnalyzerUrl = "http://ai-system-analyzer-service:5500/...";
        // ... other prod URLs
    } else if (Objects.equals(mode, "dev")) {
        this.AiSystemAnalyzerUrl = "http://localhost:5500/...";
        // ... other dev URLs
    }
}
```

---

## API Endpoints

The Director exposes the following endpoints under the base path `/api/v1/workflow`.

| Method | Path                                           | Description                                                                                                     |
| :----- | :--------------------------------------------- | :-------------------------------------------------------------------------------------------------------------- |
| `POST` | `/`                                            | **Initiates the workflow.** Accepts `.ttl` and `.txt` files as `multipart/form-data` and returns a `processId`. |
| `GET`  | `/ttl-analyze-complete/{processId}`            | Pollable endpoint. Returns `true` if the AI System Analyzer has finished.                                       |
| `GET`  | `/txt-analyze-complete/{processId}`            | Pollable endpoint. Returns `true` if the PQ Analyzer has finished.                                              |
| `POST` | `/trigger-unification-workflow/{processId}`    | Triggers the first unification step after the initial analyses are complete.                                    |
| `GET`  | `/unification-first-step-complete/{processId}` | Pollable endpoint. Returns `true` if the first unification step is done.                                        |
| `GET`  | `/unification-first-step-response/{processId}` | Retrieves the data needed for the frontend's clarification screen.                                              |
| `POST` | `/trigger-output-generation/{processId}`       | **Finalizes the workflow.** Accepts the user's clarification choices and triggers the final output generation.  |
| `GET`  | `/output-generation-complete/{processId}`      | Pollable endpoint. Returns `true` if the final ZIP file is ready.                                               |
| `GET`  | `/output-generation-response/{processId}`      | Downloads the final `output.zip` file.                                                                          |

<details>
<summary><b>Click to see a Code-Level Walkthrough of the Orchestration</b></summary>

The core logic resides in `WorkflowServiceImpl.java`.

1.  **`initiateWorkflow(...)`**:

    - Generates a `processId`.
    - Calls `delegationService.sendFileToAiSystemAnalyzer(...)` and `delegationService.sendFileToPQAnalyzer(...)` **in parallel**.
    - Uses `.thenAccept(response -> ...)` callbacks on the returned `CompletableFuture` objects. When a service responds, the callback function executes and places the result into the appropriate `HashMap` (`processIdAiSystemAnalyzerResponse` or `processIdPQAnalyzerResponse`).

2.  **`triggerUnificationWorkflow(...)`**:

    - Is called by the frontend after it confirms both initial analyses are complete.
    - Retrieves the stored `Workflow` and `List<PQAnalyzerResponseItemDTO>` from the HashMaps using the `processId`.
    - Calls `delegationService.sendFilesToUnificationFirstStep(...)` with this data.
    - Again, uses a `.thenAccept()` callback to store the result in the `processUnificationFirstStepResponse` map.

3.  **`triggerUnificationWorkflowSecondStep(...)`**:

    - Is called by the frontend with the user's final clarification choices.
    - Calls `delegationService.sendToUnificationSecondStep(...)`.
    - Its `.thenAccept()` callback stores the final `byte[]` (the ZIP file) in the `processUnificationSecondStepResponse` map.

4.  **`getOutputGenerationResponse(...)`**: - Simply retrieves the `byte[]` from the `processUnificationSecondStepResponse` map and returns it to the frontend for download.
</details>
