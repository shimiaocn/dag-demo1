package dag.exception;

public class WorkflowCycleException extends WorkflowRunException {

    public WorkflowCycleException(String message) {
        super(message);
    }

    public WorkflowCycleException(String message, Throwable cause) {
        super(message, cause);
    }

    public WorkflowCycleException(Throwable cause) {
        super(cause);
    }
}
