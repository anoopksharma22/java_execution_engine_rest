package in.goducky.execution_engine.controllers;

import in.goducky.execution_engine.dto.ExecutionResponse;
import in.goducky.execution_engine.services.DockerCodeExecutionService;
import in.goducky.execution_engine.services.CodeExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins="*")
public class HelloController {

    @Autowired
    DockerCodeExecutionService dockerCodeExecutionService;
    @Autowired
    CodeExecutor codeExecutor;

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello World");
    }

    @PostMapping("/submit")
    public ResponseEntity<ExecutionResponse> submit(@RequestBody String code) throws IOException, InterruptedException {
        ExecutionResponse output = dockerCodeExecutionService.execute(code);
        return ResponseEntity.ok(output);
    }
    @PostMapping("/execute")
    public ResponseEntity<ExecutionResponse> executeJavaCode(@RequestBody String javaCode) {
        ExecutionResponse output = codeExecutor.execute(javaCode);
        return ResponseEntity.ok(output);
    }

}
