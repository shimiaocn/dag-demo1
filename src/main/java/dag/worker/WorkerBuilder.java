package dag.worker;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class WorkerBuilder {

    public static <P, R> Builder<P, R> builder() {
        return new Builder<>();
    }

    public static class Builder<P, R> {
        private final Worker<P, R> worker = new Worker<>();

        public final WorkerBuilder.Builder<P, R> id(String id) {
            worker.setId(id);
            return this;
        }

        public final WorkerBuilder.Builder<P, R> param(P param) {
            worker.setParam(param);
            return this;
        }

        public final WorkerBuilder.Builder<P, R> workExecutor(IWorkExecutor<P, R> workExecutor) {
            worker.setWorkerExecutor(workExecutor);
            return this;
        }

        public final Worker<P, R> build() {
            return worker;
        }
    }
}
