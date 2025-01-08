package com.pathcreator.hive.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

@Slf4j
public class ExecutionTime {

    public static <T> T measureExecutionTime(String methodName, Callable<T> callable) {
        long startTime = System.currentTimeMillis();
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            log.info("Execution time for {}: {} ms", methodName, duration);
        }
    }
}