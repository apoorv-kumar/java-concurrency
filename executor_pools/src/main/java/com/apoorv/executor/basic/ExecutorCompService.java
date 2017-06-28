package com.apoorv.executor.basic;

import java.util.Random;
import java.util.concurrent.*;

public class ExecutorCompService {
    private static final int N_THREADS=20;
    private static final ExecutorService execSrv = Executors.newFixedThreadPool(N_THREADS);
    private static final ExecutorCompletionService<String> execCompSrv = new ExecutorCompletionService<String>(execSrv);

    public static void main(String[] args) {
        //create tasks
        for (int i = 0; i < N_THREADS; i++) {
            Callable<String> c = new StringProducerTask();
            execCompSrv.submit(c);
        }
        execSrv.shutdown();

        //wait for results
        //we need to remember the number of futures to expect
        for (int i = 0; i < N_THREADS; i++) {
            try {
                Future<String> f_produced = execCompSrv.take();
                System.out.println("Received: " + f_produced.get());
            } catch (InterruptedException e) {
                System.out.println("Can't take future from queue. Interrupted.");
            } catch (ExecutionException e) {
                System.out.println("Couldn't execute future.");
            }
        }


    }
}
