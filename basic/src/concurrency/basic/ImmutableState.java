package src.concurrency.basic;

//For single consumer and producer, we can achieve concurrency with volatile + immutable state object.

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Random;
/**
 * Created by apoorv on 26/2/17.
 */
public class ImmutableState {
    private static class ImmutableStateObject{
        public final long a1;
        public final long a2;

        public ImmutableStateObject(long a1, long a2){
            this.a1 = a1;
            this.a2 = a2;
        }
    }

    //Singleton
    private static class Consumer implements Runnable{
        
        private final String name;
        
        public Consumer(String name){
            this.name = name;
        }

        
        public void run(){
            try {
                while(true){
                    if(globalBuffer != null){
                        System.out.printf("Consumed by %s :  %d/%d\n", name, globalBuffer.a1, globalBuffer.a2);
                        globalBuffer = null;
                    }
                    else{
                        Thread.sleep(500);
                    }
                }
            } catch( InterruptedException e){
                System.out.println("Consumer thread interrupted");
            }
        }
    }

    // Singleton
    private static class Producer implements Runnable{

        public final String name;
        
        public Producer(String name){
            this.name = name;
        }
        
        public void run(){
            try {
                Random random = new Random();

                while(true){
                    if(globalBuffer == null){
                        ImmutableStateObject newObj = new ImmutableStateObject(random.nextInt(100), random.nextInt(100));
                        System.out.printf("Produced by %s :  %d/%d\n", name, newObj.a1, newObj.a2);
                        globalBuffer = newObj;
                    }
                    else{
                        Thread.sleep(random.nextInt(1000));
                    }
                }
            } catch( InterruptedException e){
                System.out.println("Producer thread interrupted");
            }
        }
    }
    
    //this is thread safe with singleton consumer and producer
    // because it's immutable.
    //also, this can be directly referenced by an
    //inner class.
    private static volatile ImmutableStateObject globalBuffer = null;


    public static void main(String[] args) {
        Thread t_consumer = new Thread(new Consumer("consumer x"));
        Thread t_producer = new Thread(new Producer("producer x"));

        t_consumer.start();
        t_producer.start();

        try{
            Thread.sleep(10000);
        } catch (InterruptedException e){
            System.out.println("main thread sleep interrupted");
        }

        System.out.println("killing consumer");
        t_consumer.stop();

        try{
            Thread.sleep(2000);
        } catch (InterruptedException e){
            System.out.println("main thread sleep interrupted");
        }

        System.out.println("killing producer");
        t_producer.stop();
    }
}
