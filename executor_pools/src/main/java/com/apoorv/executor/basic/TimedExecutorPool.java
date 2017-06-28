package com.apoorv.executor.basic;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


public class TimedExecutorPool {

    private final static int NTHREADS=30;
    private final static ExecutorService execSrv = Executors.newFixedThreadPool(NTHREADS);
    private static AtomicInteger cancelledFutures = new AtomicInteger(0);
    public static void main(String[] args) {
        //created tasks
        ArrayList<StringProducerTask> taskList = new ArrayList<StringProducerTask>();
        for (int i = 0; i < NTHREADS; i++)
            taskList.add(new StringProducerTask());

        try {
            List<Future<String>> resultFutures = execSrv.invokeAll(taskList, 2000, TimeUnit.MILLISECONDS);
            execSrv.shutdown();
            for(Future<String> f : resultFutures){
                try {
                    String result = f.get();
                    System.out.println("Received future result: " + result);
                } catch (ExecutionException e) {
                    System.out.println("Future couldn't execute");
                } catch (CancellationException e){
                    cancelledFutures.incrementAndGet();
                }
            }
        } catch (InterruptedException e) {
            System.out.println("pool was interrupted");
        }

        System.out.println(cancelledFutures.toString() + " futures were cancelled (most likely timeout)");

    }
}
