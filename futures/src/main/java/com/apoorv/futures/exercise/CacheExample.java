package com.apoorv.futures.exercise;

import java.util.concurrent.*;

/**
 * Created by apoorv on 4/29/17.
 *
 * Building scalable and efficient cache from Goetz.
 *
 * We ensure once-only compute for each value with minimal locking across threads.
 * handle errors associated with futures
 */

interface Computable<A,V>{
    V compute(A input) throws InterruptedException;
}

class Fibonacci implements Computable<Long, Long>{
    //poor implementation representing slow func
    public Long compute(Long input) throws InterruptedException {
        if(input == 0 || input == 1) return input;

        Computable<Long,Long> fib = new Fibonacci();
        return(fib.compute(input-2) + fib.compute(input-1));
    }
}

class ComputeCache<A,V> implements Computable<A,V>{
    private ConcurrentHashMap<A, Future<V>> map;
    private Computable<A,V> cacheCompute;

    ComputeCache(Computable<A,V> c){
        map = new ConcurrentHashMap<A, Future<V>>();
        cacheCompute = c;
    }

    public V compute(final A input) throws InterruptedException{

        Future<V> finalFuture = null;
        while(true){
            if(!map.containsKey(input)){
                Callable<V> computeCallable = new Callable<V>() {
                    public V call() throws InterruptedException {
                        return(cacheCompute.compute(input));
                    }
                };

                FutureTask<V> computeFuture = new FutureTask(computeCallable);
                Future<V> existingFuture = map.putIfAbsent(input, computeFuture);

                if(existingFuture == null) {
                    finalFuture = computeFuture;
                    computeFuture.run();
                }
            }

            try{
                return finalFuture.get();
            } catch (ExecutionException e) {
                System.out.println(e.getCause().getStackTrace().toString());
            } catch (CancellationException e){
                //remove the entry if compute is cancelled
                map.remove(input);
            }
        }
    }
}

public class CacheExample {

    private static class FibRunnable implements Runnable{

        Computable<Long,Long> func;
        Long arg;

        FibRunnable(Long arg, Computable<Long,Long> func){
            this.func = func;
            this.arg = arg;
        }

        public void run() {
            try {
                long startTime = System.currentTimeMillis();
                Long output = func.compute(arg);
                long endTime = System.currentTimeMillis();
                System.out.printf("Result fib(%6d) = %20d. Compute time %4d ms\n", arg, output, endTime-startTime);
            } catch (InterruptedException e) {
                System.out.println( Thread.currentThread().getName() + " interrupted.");
            }
        }
    }

    public static void main(String[] args) {
        Computable<Long,Long> fibonacci = new Fibonacci();
        ComputeCache<Long,Long> fibonacciCached = new ComputeCache(fibonacci);

        FibRunnable fibRunnable1 = new FibRunnable(40L, fibonacci);
        FibRunnable fibRunnable2 = new FibRunnable(40L, fibonacci);
        FibRunnable fibRunnable3 = new FibRunnable(40L, fibonacci);

        Thread t1 = new Thread(fibRunnable1);
        Thread t2 = new Thread(fibRunnable2);
        Thread t3 = new Thread(fibRunnable3);

        long startTime = System.currentTimeMillis();
        t1.start(); t2.start(); t3.start();


        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("total time: " + Long.toString(endTime-startTime));
    }
}
