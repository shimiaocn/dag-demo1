package dag.worker;

import com.google.mu.function.CheckedSupplier;
import dag.threadpool.DefaultThreadPool;
import dag.worker.result.ResultState;
import dag.worker.result.WorkResult;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import static dag.worker.result.WorkResult.defaultResult;

@Slf4j
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
public class Worker<Param, Result> {

    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private Param param;

    @Getter
    private IWorkExecutor<Param, Result> workerExecutor;

    private Executor executor = DefaultThreadPool.ASYNC_DEFAULT_EXECUTOR;

    @Setter
    @Getter
    private Set<Worker<?, ?>> depends = new LinkedHashSet<>();

    // 用来存临时的结果
    @Getter
    @Setter
    private volatile WorkResult<Result> workResult = defaultResult();

    /**
     * 标记该事件是否已经被处理过了，譬如已经超时返回false了，后续rpc又收到返回值了，则不再二次回调
     * 经试验,volatile并不能保证"同一毫秒"内,多线程对该值的修改和拉取
     * <p>
     * 0-init, 1-finish, 2-error, 3-working
     */
    private AtomicInteger state = new AtomicInteger(0);

    private static final int INIT = 0;
    private static final int FINISH = 1;
    private static final int ERROR = 2;
    private static final int WORKING = 3;

    public Worker(String id, Param param, IWorkExecutor<Param, Result> workerExecutor) {
        if (workerExecutor == null) {
            throw new NullPointerException("worker executor is null");
        }
        this.id = id;
        this.param = param;
        this.workerExecutor = workerExecutor;
    }

    public void setWorkerExecutor(IWorkExecutor<Param, Result> workerExecutor) {
        if (workerExecutor == null) {
            throw new NullPointerException("worker executor is null");
        }
        this.workerExecutor = workerExecutor;
    }

    private int getState() {
        return state.get();
    }

    public boolean addDepend(Worker<?, ?> depend) {
        return this.depends.add(depend);
    }

    public boolean addDepends(Collection<Worker<?, ?>> depends) {
        return this.depends.addAll(depends);
    }

    public void init() {
        this.workResult = defaultResult();
        this.state.set(INIT);
    }

    public WorkResult<Result> execute() {
        // 避免重复执行
        if (!checkIsNullResult()) {
            return workResult;
        }
        try {
            // 如果已经不是init状态了，说明正在被执行或已执行完毕。这一步很重要，可以保证任务不被重复执行
            if (!compareAndSetState(INIT, WORKING)) {
                return workResult;
            }
            // 执行业务逻辑
            log.info("start to execute worker: [{}]", id);
            CompletableFuture<Result> resultFuture = new CompletableFuture<>();
            executor.execute(() -> execute(() -> workerExecutor.run(param), resultFuture));
            workResult.setResult(resultFuture.get(10, TimeUnit.SECONDS));
            workResult.setResultState(ResultState.SUCCESS);
            compareAndSetState(WORKING, FINISH);
            return workResult;
        } catch (TimeoutException exception) {
            if (!checkIsNullResult()) {
                return workResult;
            }
            constructTimeoutResult(WORKING, exception);
            return workResult;
        } catch (Exception e) {
            if (!checkIsNullResult()) {
                return workResult;
            }
            constructFailResult(WORKING, e);
            return workResult;
        }
    }

    private CompletableFuture<Result> execute(CheckedSupplier<Result, Exception> supplier, CompletableFuture<Result> future) {
        try {
            future.complete(supplier.get());
        } catch (Exception e) {
            log.error("workflow run error", e);
            future.completeExceptionally(e);
        }
        return future;
    }

    private boolean checkIsNullResult() {
        return ResultState.DEFAULT == workResult.getResultState();
    }

    private boolean compareAndSetState(int expect, int update) {
        return this.state.compareAndSet(expect, update);
    }

    private void constructTimeoutResult(int expect, Exception e) {
        // 试图将它从expect状态,改成Error
        if (!compareAndSetState(expect, ERROR)) {
            return;
        }
        // 尚未处理过结果
        if (checkIsNullResult()) {
            if (e == null) {
                workResult = defaultResult();
            } else {
                workResult = defaultTimeoutResult(e);
            }
        }
    }

    private void constructFailResult(int expect, Exception e) {
        // 试图将它从expect状态,改成Error
        if (!compareAndSetState(expect, ERROR)) {
            return;
        }
        // 尚未处理过结果
        if (checkIsNullResult()) {
            if (e == null) {
                workResult = defaultResult();
            } else {
                workResult = defaultExResult(e);
            }
        }
    }

    private WorkResult<Result> defaultExResult(Exception ex) {
        workResult.setResultState(ResultState.EXCEPTION);
        workResult.setResult(null);
        workResult.setEx(ex);
        return workResult;
    }

    private WorkResult<Result> defaultTimeoutResult(Exception ex) {
        workResult.setResultState(ResultState.TIMEOUT);
        workResult.setResult(null);
        workResult.setEx(ex);
        return workResult;
    }
}
