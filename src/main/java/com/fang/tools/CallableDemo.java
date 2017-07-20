package com.fang.tools;

/**
 * Created by user on 2017/7/19.
 */
//: concurrency/CallableDemo.java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.*;


class TaskWithResult implements Callable<String> {
    private int id;
    public TaskWithResult(int id) {
        this.id = id;
    }
    public String call() {
        try {
            CallableDemo.logger.info("begin call "+Thread.currentThread().getName());
            TimeUnit.SECONDS.sleep(CallableDemo.TIME_OUT_TEST1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "result of TaskWithResult " + id;
    }
}

public class CallableDemo {
    public static  final Logger logger = LoggerFactory.getLogger(CallableDemo.class);
    public final static Integer TIME_OUT_TEST = 20;
    public final static Integer TIME_OUT_TEST1= 10;
    public static void main(String[] args) {
        ExecutorService exec = Executors.newCachedThreadPool();
        ArrayList<Future<String>> results =
                new ArrayList<Future<String>>();
        for(int i = 0; i < 10; i++)
            results.add(exec.submit(new TaskWithResult(i)));
        logger.info(" begin ...");
        for(Future<String> fs : results)
            try {
                // get() blocks until completion:
                logger.info(fs.get(CallableDemo.TIME_OUT_TEST,TimeUnit.SECONDS));
            } catch(InterruptedException e) {
                logger.info(e.getMessage(),e);
                return;
            } catch(ExecutionException e) {
                logger.info(e.getMessage(),e);
            } catch (TimeoutException e) {
                logger.info(e.getMessage(),e);
            } finally {
                exec.shutdown();
            }
    }
}