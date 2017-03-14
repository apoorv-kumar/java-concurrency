package src.concurrency.basic;

/**
 * Created by apoorv on 11/3/17.
 */

import java.util.Random;

//For single consumer and producer, we can achieve concurrency with volatile + immutable state object.

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * Created by apoorv on 26/2/17.
 */
public class WaitNotify {


    //Singleton
    private static class Consumer implements Runnable{


        public void run(){
            //NOTE: we can't use getName in the constructor, only in run.
            //The constructor is called from 'main' thread.
            String name = Thread.currentThread().getName();
            try {
                while(true){
                    synchronized (globalBuffer){
                        if(!(globalBuffer.get() == -1)){
                            System.out.printf("Consumed by %s :  %s\n", name, globalBuffer.toString());
                            //NOTE: we can't make the ref point to new object because
                            //of the wait notify mechanism. If we change it, the notify
                            //would go to an entirely different object!
                            globalBuffer.set(-1);
                        }
                        else{
                            System.out.println( name + " waiting...");
                            //this frees the lock, and waits for a notification.
                            //We can also specify a wait timeout.
                            globalBuffer.wait(131);
                            System.out.println( name + " notified or timed out.");
                        }
                    }
                }
            } catch( InterruptedException e){
                System.out.println("Consumer thread interrupted. Exiting.");
            }
        }
    }

    // Singleton
    private static class Producer implements Runnable{

        public void run(){
            //NOTE: we can't use getName in the constructor, only in run.
            //The constructor is called from 'main' thread.
            String name = Thread.currentThread().getName();
            try {
                Random random = new Random();
                while(true){
                    synchronized (globalBuffer){
                        if(globalBuffer.get() == -1){
                            int newVal = random.nextInt(100);
                            System.out.printf("Produced by %s :  %d\n", name, newVal);
                            //NOTE: we can't make the ref point to new object because
                            //of the wait notify mechanism. If we change it, the notify
                            //would go to an entirely different object!
                            globalBuffer.set(newVal);
                            //globalBuffer.notify();
                        }
                        else{
                            System.out.println(name + " waiting...");
                            globalBuffer.wait();
                            System.out.println(name + " notified...");
                        }
                    }
                    Thread.sleep(600);
                }
            } catch( InterruptedException e){
                System.out.println("Producer thread interrupted.Exiting.");
            }
        }
    }

    //this is thread safe with singleton consumer and producer
    // because it's immutable.
    //also, this can be directly referenced by an
    //inner class.
    // -1 represents invalid state.
    private static AtomicInteger globalBuffer = new AtomicInteger(-1);


    public static void main(String[] args) {
        Thread t_consumer = new Thread(new Consumer());
        Thread t_producer = new Thread(new Producer());

        t_consumer.start();
        t_producer.start();

        try{
            Thread.sleep(10000);
        } catch (InterruptedException e){
            System.out.println("main thread sleep interrupted");
        }
        System.out.println("Interrupting producer");
        t_producer.interrupt();

        System.out.println("Interrupting consumer");
        t_consumer.interrupt();

    }
}
