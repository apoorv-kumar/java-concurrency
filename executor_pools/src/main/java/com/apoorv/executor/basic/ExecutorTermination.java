package com.apoorv.executor.basic;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutorTermination {
    private static final int N_THREADS = 100;
    private static final ExecutorService executor = Executors.newFixedThreadPool(N_THREADS);
    private static AtomicInteger intr_count = new AtomicInteger(0);

    private static void rand_waiter(){
        Random r = new Random();
        int wait = r.nextInt(2000);

        try {
            Thread.sleep(wait);
        } catch (InterruptedException e) {
            intr_count.incrementAndGet();
            System.out.println("thread " + Thread.currentThread().getName() + " interrrupted.");
        }
        System.out.println("thread " + Thread.currentThread().getName() + " completed.");

    }

    public static void main(String[] args) throws InterruptedException {
        ArrayList<Runnable> r_list = new ArrayList<Runnable>();
        for (int i = 0; i < 200; i++) {
            r_list.add(new Runnable() {
                public void run() {
                    rand_waiter();
                }
            });
        }

        for(Runnable r: r_list)
            executor.execute(r);

        //print status
        executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
        //as opposed to shutdown, which won't interrupt, only stop
        //accepting any more tasks.
        executor.shutdownNow();
        System.out.println(intr_count.toString() + " threads were interrupted");

    }

}
