package dag.flow;

import dag.context.FlowContext;
import dag.exception.WorkflowCycleException;
import dag.threadpool.DefaultThreadPool;
import dag.worker.Worker;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Data
@Slf4j
public class Workflow {

    private FlowContext flowContext;

    private Executor executor = DefaultThreadPool.ASYNC_DEFAULT_EXECUTOR;

    private long timeout;

    private TimeUnit unit;

    private final Map<String, Worker<?, ?>> workerMap = new HashMap<>();

    public Collection<Worker<?, ?>> getWorkers() {
        return workerMap.values();
    }

    public Worker<?, ?> addWorker(Worker<?, ?> worker) {
        String workerId = worker.getId();
        if (workerMap.containsKey(workerId)) {
            return workerMap.get(workerId);
        }
        workerMap.put(workerId, worker);
        return worker;
    }

    public Worker<?, ?> getWorkerById(String workerId) {
        return workerMap.get(workerId);
    }

    public void run() throws ExecutionException, InterruptedException {
        Collection<Worker<?, ?>> workers = workerMap.values();
        boolean hasCycle = FlowChecker.hasCycle(workers);
        if (hasCycle) {
            throw new WorkflowCycleException("the flow has cycle");
        }
        CompletableFuture<?>[] futures = new CompletableFuture[workers.size()];
        int i = 0;
        //配置所有节点依赖关系
        for (Worker<?, ?> worker : workers) {
            if (CollectionUtils.isEmpty(worker.getDepends())) {
                log.info("start worker direct: " + worker.getId());
                futures[i++] = CompletableFuture.runAsync(worker::execute);
            } else {
                CompletableFuture<?>[] dependFutures = worker.getDepends().stream()
                        .map(dependWorker -> CompletableFuture.runAsync(dependWorker::execute))
                        .toArray(CompletableFuture[]::new);
                for (CompletableFuture<?> completableFuture : dependFutures) {
                    futures[i++] = completableFuture;
                }
                //boolean cancel = false;
                //CompletableFuture.anyOf(dependFutures).exceptionally(e-> {
                //    cancel=true;
                //}).get();
                CompletableFuture.allOf(dependFutures).thenRun(worker::execute);
            }
        }
        CompletableFuture.allOf(futures).get();
    }

}
