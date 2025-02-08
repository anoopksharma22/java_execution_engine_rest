package in.goducky.execution_engine.services;

public class CompliationOutput {
    public boolean success;
    public String message;
    public CompliationOutput(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
