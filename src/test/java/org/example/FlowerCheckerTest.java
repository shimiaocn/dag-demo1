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

public class FlowerCheckerTest {

    @Test
    public void noCycle1() {
        Worker<String, String> w1 = WorkerBuilder.<String, String>builder()
                .id("w1")
                .param("100")
                .workExecutor(new TestWorkExecutor())
                .build();

        Worker<String, String> w2 = WorkerBuilder.<String, String>builder()
                .id("w2")
                .param("1")
                .workExecutor(new TestWorkExecutor())
                .build();

        Workflow test = WorkflowBuilder.builder()
                .addDependencies(w1, w2).build();
        boolean hasCycle = FlowChecker.hasCycle(test.getWorkers());
        Assert.assertFalse(hasCycle);
    }

    @Test
    public void noCycle2() {
        Worker<String, String> w1 = WorkerBuilder.<String, String>builder()
                .id("w1")
                .param("100")
                .workExecutor(new TestWorkExecutor())
                .build();

        Worker<String, String> w2 = WorkerBuilder.<String, String>builder()
                .id("w2")
                .param("1")
                .workExecutor(new TestWorkExecutor())
                .build();

        Worker<String, String> w3 = WorkerBuilder.<String, String>builder()
                .id("w3")
                .param("1")
                .workExecutor(new TestWorkExecutor())
                .build();

        Workflow test = WorkflowBuilder.builder()
                .addDependencies(w1, w2)
                .addDependencies(w1, w3)
                .addDependencies(w3, w2)
                .build();
        boolean hasCycle = FlowChecker.hasCycle(test.getWorkers());
        Assert.assertFalse(hasCycle);
    }

    @Test
    public void hasCycle1() {
        Worker<String, String> w1 = WorkerBuilder.<String, String>builder()
                .id("w1")
                .param("100")
                .workExecutor(new TestWorkExecutor())
                .build();

        Worker<String, String> w2 = WorkerBuilder.<String, String>builder()
                .id("w2")
                .param("1")
                .workExecutor(new TestWorkExecutor())
                .build();

        Worker<String, String> w3 = WorkerBuilder.<String, String>builder()
                .id("w3")
                .param("1")
                .workExecutor(new TestWorkExecutor())
                .build();

        List<Worker<?, ?>> workers = Arrays.asList(w2, w3);

        Workflow test = WorkflowBuilder.builder()
                .addDependencies(w1, w2)
                .addDependencies(w2, w3)
                .addDependencies(w3, w1)
                .build();
        boolean hasCycle = FlowChecker.hasCycle(test.getWorkers());
        Assert.assertTrue(hasCycle);
    }

    @Test
    public void hasCycle2() {
        Worker<String, String> w1 = WorkerBuilder.<String, String>builder()
                .id("w1")
                .param("100")
                .workExecutor(new TestWorkExecutor())
                .build();

        Workflow test = WorkflowBuilder.builder()
                .addDependencies(w1, w1)
                .build();
        boolean hasCycle = FlowChecker.hasCycle(test.getWorkers());
        Assert.assertTrue(hasCycle);
    }
}
