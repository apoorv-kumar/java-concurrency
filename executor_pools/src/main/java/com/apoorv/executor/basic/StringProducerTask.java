package com.apoorv.executor.basic;

import java.util.Random;
import java.util.concurrent.Callable;


public class StringProducerTask implements Callable<String> {
    public String call() throws Exception {
        Random r = new Random();
        Thread.sleep(r.nextInt(3000));
        return "Produced by " + Thread.currentThread().getName();
    }
}
