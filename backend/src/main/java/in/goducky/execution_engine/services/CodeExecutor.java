package in.goducky.execution_engine.services;

import in.goducky.execution_engine.dto.ExecutionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class CodeExecutor {

    private static final String CLASS_NAME = "Main";
    private static final String JAVA_FILE_NAME = CLASS_NAME + ".java";
    Logger log = LoggerFactory.getLogger(CodeExecutor.class);

    public ExecutionResponse execute(String javaCode) {
//        log.info("Executing " + javaCode);
        Path path = null;
        try {
            // Write the Java code to a file
            path = writeToFile(javaCode);
            log.info("path " + path);
            // Compile the Java code
            if (!compile(path)) {
                return new ExecutionResponse("", "Compilation failed. Check your syntax.");
            }

            // Execute and return output
            return run(path);

        } catch (Exception e) {
            return new ExecutionResponse("", e.getMessage());
        } finally {
            try {
                assert path != null;
                if (Files.deleteIfExists(path)) {
                    System.out.println("File deleted successfully: " + path);
                } else {
                    System.out.println("File does not exist: " + path);
                }
            } catch (IOException e) {
                System.err.println("Failed to delete file: " + e.getMessage());
            }
        }
    }

    // Method to write user-submitted Java code to a file
    public Path writeToFile(String code) throws IOException {
        Path tempDir = Paths.get("temp").toAbsolutePath();
        if (!Files.exists(tempDir)) {
            Files.createDirectories(tempDir);
        }
        Path file = tempDir.resolve(JAVA_FILE_NAME);
        Files.createFile(file);
        Files.write(file, code.getBytes());
        return file;
    }

    // Method to compile Java code dynamically
    public boolean compile(Path path) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        return compiler.run(null, null, null, path.toString()) == 0;
    }

    // Method to run the compiled Java class
    public ExecutionResponse run(Path path) throws Exception {
        log.info("Executing {}", path);
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-cp", path.getParent().toString(), CLASS_NAME);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        // Capture output -- non-blocking way
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
                log.error(e.getMessage());
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    stderr.append(line).append("\n");
                }
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        });
        executor.shutdown();
        process.waitFor();
        return new ExecutionResponse(stdout.toString().trim(), stderr.toString().trim());
    }
}