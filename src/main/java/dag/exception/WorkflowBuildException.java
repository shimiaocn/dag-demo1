package dag.exception;

public class WorkflowBuildException extends RuntimeException {

    public WorkflowBuildException(String message) {
        super(message);
    }

    public WorkflowBuildException(String message, Throwable cause) {
        super(message, cause);
    }

    public WorkflowBuildException(Throwable cause) {
        super(cause);
    }
}
