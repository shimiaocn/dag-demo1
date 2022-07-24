package org.example;

import dag.worker.Worker;
import dag.worker.WorkerBuilder;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        //Worker<Integer, String> worker1 = Worker
        //        .<Integer, String>builder()
        //        .id("id")
        //        .param(25)
        //        .workExecutor(new TestWorkExecutor())
        //        .build();
        //worker1.execute();
        Worker<String, String> test = WorkerBuilder.<String, String>builder()
                .id("test")
                .param("22")
                .workExecutor(new TestWorkExecutor())
                .build();
        test.execute();
        System.out.println();
    }
}
