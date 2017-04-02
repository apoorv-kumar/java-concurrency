package src.concurrency.basic;

import java.util.Random;

public class SampleStringProducer implements StringProducer {
    private int data = 0;
    private int avgDelay = 0;
    public String produce(){

        String name = Thread.currentThread().getName();
            /*
            Throws: InterruptedException - if any thread has interrupted the current thread. The interrupted status of the current thread is cleared when this exception is thrown.
             */
        try{
            Random r = new Random();
            int wait = r.nextInt(2*avgDelay);
            if(wait != 0) Thread.sleep(wait);
            data++;
        } catch (InterruptedException e){
            System.out.println("producer in " + name + " interrupted.");
            //Set interrupted so code higher up can see it.
            Thread.currentThread().interrupt();
        }
        return(name + "-" + Integer.toString(data));
    }


    public SampleStringProducer(int avgDelay){
        this.avgDelay = avgDelay;
    }

}
