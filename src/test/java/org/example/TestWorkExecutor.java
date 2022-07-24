package org.example;

import dag.worker.IWorkExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class TestWorkExecutor implements IWorkExecutor<String, String> {
    @Override
    public String run(String param) throws Exception {
        Thread.sleep(3000L);
        System.out.println(new Date() + param);
        //log.info("test---");
        //int result = integer/0;
        return "hhh";
    }
}
