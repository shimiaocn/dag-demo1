package org.example;

import dag.flow.FlowChecker;
import dag.flow.Workflow;
import dag.flow.WorkflowBuilder;
import dag.worker.Worker;
import dag.worker.WorkerBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class WorkFlowTest {

    @Test
    public void test() throws ExecutionException, InterruptedException {
        Worker<String, String> w1 = WorkerBuilder.<String, String>builder()
                .id("w1")
                .param("w1")
                .workExecutor(new TestWorkExecutor())
                .build();

        Worker<String, String> w2 = WorkerBuilder.<String, String>builder()
                .id("w2")
                .param("w2")
                .workExecutor(new TestWorkExecutor())
                .build();

        Worker<String, String> w3 = WorkerBuilder.<String, String>builder()
                .id("w3")
                .param("w3")
                .workExecutor(new TestWorkExecutor())
                .build();

        List<Worker<?, ?>> workers = Arrays.asList(w2, w3);

        Workflow test = WorkflowBuilder.builder()
                .addDependencies(w1, w2)
                //.addDependencies(w2, w3)
                .addDependencies(w3, w2)
                .build();
        boolean hasCycle = FlowChecker.hasCycle(test.getWorkers());
        Assert.assertFalse(hasCycle);
        test.run();
        System.out.println();
    }
}
