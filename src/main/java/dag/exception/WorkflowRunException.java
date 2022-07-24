package dag.exception;

public class WorkflowRunException extends RuntimeException {

    public WorkflowRunException(String message) {
        super(message);
    }

    public WorkflowRunException(String message, Throwable cause) {
        super(message, cause);
    }

    public WorkflowRunException(Throwable cause) {
        super(cause);
    }
}
