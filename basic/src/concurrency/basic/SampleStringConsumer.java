package src.concurrency.basic;

import java.util.Random;

public class SampleStringConsumer implements StringConsumer{
    private int avgDelay;
    public void consume(String data){
        String name = Thread.currentThread().getName();

        try{
            Random r = new Random();
            int wait = r.nextInt(2*avgDelay);
            Thread.sleep(wait);
            System.out.println("consumer in " + name + " consumed data: " + data);
        } catch (InterruptedException e){
            System.out.println("consumer in " + name + " interrupted.");
            Thread.currentThread().interrupt();
        }
    }

    public SampleStringConsumer(int avgDelay){
        this.avgDelay = avgDelay;
    }
}
