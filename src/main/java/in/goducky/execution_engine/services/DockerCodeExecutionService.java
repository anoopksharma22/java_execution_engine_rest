package in.goducky.execution_engine.services;


import in.goducky.execution_engine.dto.ExecutionResponse;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class DockerCodeExecutionService {

    public ExecutionResponse execute(String code) throws IOException, InterruptedException {
        Path tempDir = Paths.get("temp").toAbsolutePath();;
        if (!Files.exists(tempDir)) {
            Files.createDirectories(tempDir);
        }
        Path file = tempDir.resolve("Main.java");

        // Write the code string into the file
        Files.createFile(file);
        Files.write(file, code.getBytes());

        // Return the file path (optional)
//        return tempFile.toString();
        ExecutionResponse output = runJavaInDocker(file);
        try {
            if (Files.deleteIfExists(file)) {
                System.out.println("File deleted successfully: " + file);
            } else {
                System.out.println("File does not exist: " + file);
            }
        } catch (IOException e) {
            System.err.println("Failed to delete file: " + e.getMessage());
        }
        return output;
    }
    private ExecutionResponse runJavaInDocker(Path filePath) throws IOException, InterruptedException {
        String mainFileName = filePath.getFileName().toString();
        Path tempDir = Paths.get("./temp").toAbsolutePath().normalize();
        ProcessBuilder processBuilder = new ProcessBuilder(
                "C:\\Program Files\\Docker\\Docker\\Resources\\bin\\docker.exe",
                "run", "--rm",
                "-v", tempDir.toString() + ":/app",
                "-w", "/app",
                "openjdk:latest",
                "sh", "-c", "javac Main.java && java Main"
        );
//        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        // Capture output
        StringBuilder stdout = new StringBuilder();
        StringBuilder stderr = new StringBuilder();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    stdout.append(line).append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    stderr.append(line).append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        executor.shutdown();
        process.waitFor();

        ExecutionResponse executionResponse = new ExecutionResponse(stdout.toString().trim(), stderr.toString().trim());
        return executionResponse;
    }
}
