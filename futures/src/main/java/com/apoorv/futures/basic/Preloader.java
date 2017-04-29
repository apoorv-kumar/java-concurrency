package com.apoorv.futures.basic;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created by apoorv on 4/16/17.
 */
public class Preloader {

    private static class SlowInitClass{
        SlowInitClass(){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println("failed to sleep");
            }
        }
    }

    private static class PreloadableTask implements Callable<Integer> {
        public Integer call() throws Exception {
            Random rand = new Random();
            Thread.sleep(300);
            return rand.nextInt();
        }
    }

    public static void main(String[] args) {
        //Pre-load
        FutureTask<Integer> ft = new FutureTask<Integer>(new PreloadableTask());
        ft.run();

        long startTime = System.nanoTime();
        SlowInitClass slowObj = new SlowInitClass();
        try {
            Integer val = ft.get();
        } catch (InterruptedException e) {
            System.out.println("interrupted");
        } catch (ExecutionException e) {
            System.out.println("exec exception");
        }
        long endTime = System.nanoTime();

        System.out.printf("Completed in %d ms", (endTime - startTime)/1000000);

    }

}
