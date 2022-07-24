package dag.threadpool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DefaultThreadPool {
    public static ExecutorService ASYNC_DEFAULT_EXECUTOR = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors() * 2,
            Runtime.getRuntime().availableProcessors() * 8,
            5, TimeUnit.MINUTES,
            new LinkedBlockingQueue<>(Runtime.getRuntime().availableProcessors() * 16),
            new ThreadFactoryBuilder().setNameFormat("workflow_async_default-%d").build(),
            new CallerRunsPolicyWithMonitor());

    private static class CallerRunsPolicyWithMonitor extends ThreadPoolExecutor.CallerRunsPolicy {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            log.warn("CallerRunsPolicyWithMonitor rejectedExecution");
            super.rejectedExecution(r, e);
        }
    }
}
