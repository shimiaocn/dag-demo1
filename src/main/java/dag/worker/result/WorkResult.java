package dag.worker.result;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkResult<Result> {
    private Result result;
    private ResultState resultState;
    private Exception ex;

    public WorkResult(Result result, ResultState resultState) {
        this.result = result;
        this.resultState = resultState;
    }

    public static <Result> WorkResult<Result> defaultResult() {
        return new WorkResult<>(null, ResultState.DEFAULT);
    }
}
