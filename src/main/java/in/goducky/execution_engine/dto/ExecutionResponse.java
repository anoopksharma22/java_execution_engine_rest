package in.goducky.execution_engine.dto;

public class ExecutionResponse {
    private String output;
    private String error;
    public ExecutionResponse(String output, String error) {
        this.output = output;
        this.error = error;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "ExecutionResponse{" +
                "output='" + output + '\'' +
                ", error='" + error + '\'' +
                '}';
    }
}
