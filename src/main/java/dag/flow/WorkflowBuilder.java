package dag.flow;

import dag.exception.WorkflowBuildException;
import dag.worker.Worker;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

public class WorkflowBuilder {

    public static WorkflowBuilder.Builder builder() {
        return new WorkflowBuilder.Builder();
    }

    public static class Builder {
        private final Workflow workflow = new Workflow();

        //private Worker<?, ?> currentWorker;

        public final Builder workerTimeout(long timeout, TimeUnit unit) {
            workflow.setTimeout(timeout);
            workflow.setUnit(unit);
            return this;
        }

        public final Builder addDependencies(Worker<?, ?> worker, Worker<?, ?>... depends) {
            if (!workerValid(worker)) {
                throw new WorkflowBuildException("the worker is not valid");
            }
            Worker<?, ?> addWorker = workflow.addWorker(worker);
            for (Worker<?, ?> depend : depends) {
                if (!workerValid(depend)) {
                    throw new WorkflowBuildException("the worker is not valid");
                }
                Worker<?, ?> dependWorker = workflow.addWorker(depend);
                addWorker.addDepend(dependWorker);
            }
            return this;
        }

        private boolean workerValid(Worker<?, ?> worker) {
            return worker != null && !StringUtils.isBlank(worker.getId());
        }

        //public final Builder addWorker(Worker<?, ?> worker) {
        //    currentWorker = workflow.addWorker(worker);
        //    return this;
        //}
        //
        //public final Builder dependOn(String... depends) {
        //    if (this.currentWorker == null) {
        //        throw new WorkflowBuildException("please add node");
        //    }
        //    List<Worker<?, ?>> dependWorkers = Lists.newArrayList();
        //    for (String depend : depends) {
        //        Worker<?, ?> dependWorker = Optional.ofNullable(workflow.getWorkerById(depend))
        //                .orElseThrow(() -> new WorkflowBuildException("cannot ref unbind node: " + depend));
        //        dependWorkers.add(dependWorker);
        //    }
        //    this.currentWorker.addDepends(dependWorkers);
        //    return this;
        //}
        //
        //public final Builder dependOn(Worker<?, ?>... depends) {
        //    if (this.currentWorker == null) {
        //        throw new WorkflowBuildException("please add node");
        //    }
        //    List<Worker<?, ?>> dependWorkers = Lists.newArrayList();
        //    for (Worker<?, ?> depend : depends) {
        //        if (depend == null || StringUtils.isBlank(depend.getId())) {
        //            throw new WorkflowBuildException("the worker or the worker id may be error");
        //        }
        //        Worker<?, ?> workerById = workflow.getWorkerById(depend.getId());
        //        if (workerById == null) {
        //            workflow.addWorker(depend);
        //            dependWorkers.add(depend);
        //        } else {
        //            dependWorkers.add(depend);
        //        }
        //    }
        //    this.currentWorker.addDepends(dependWorkers);
        //    return this;
        //}

        public final Workflow build() {
            return workflow;
        }
    }
}
