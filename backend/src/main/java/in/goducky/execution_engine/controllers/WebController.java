package in.goducky.execution_engine.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class WebController {
    @GetMapping("/{path:[^.]*}") // Matches everything except files with extensions
    public String forwardReactRoutes(@PathVariable String path) {
        return "forward:/index.html";
    }
}
