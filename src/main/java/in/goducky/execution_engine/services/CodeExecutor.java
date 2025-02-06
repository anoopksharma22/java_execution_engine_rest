package in.goducky.execution_engine.services;

import in.goducky.execution_engine.dto.ExecutionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class CodeExecutor {

    private static final String CLASS_NAME = "Main";
    private static final String JAVA_FILE_NAME = CLASS_NAME + ".java";

    public ExecutionResponse execute(String javaCode) {
        try {
            // Write the Java code to a file
            writeToFile(javaCode);

            // Compile the Java code
            if (!compile()) {
                return new ExecutionResponse("", "Compilation failed. Check your syntax.");
            }

            // Execute and return output
            return run();

        } catch (Exception e) {
            return new ExecutionResponse("", e.getMessage());
        }
    }
    // Method to write user-submitted Java code to a file
    public void writeToFile(String code) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(JAVA_FILE_NAME))) {
            writer.write(code);
        }
    }

    // Method to compile Java code dynamically
    public boolean compile() {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        return compiler.run(null, null, null, JAVA_FILE_NAME) == 0;
    }

    // Method to run the compiled Java class
    public ExecutionResponse run() throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder("java", CLASS_NAME);
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
        return new ExecutionResponse(stdout.toString().trim(), stderr.toString().trim());
    }
}