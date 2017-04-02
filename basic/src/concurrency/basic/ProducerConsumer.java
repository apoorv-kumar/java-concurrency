package src.concurrency.basic;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class ProducerConsumer {

    // NOTE: Single thread confinement is ensured because String is immutable.
    private static final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private static class Producer implements Runnable{

        public void run(){
            StringProducer producer = new SampleStringProducer(250);
            String name = Thread.currentThread().getName();
            /*
            Throws: InterruptedException - if any thread has interrupted the current thread. The interrupted status of the current thread is cleared when this exception is thrown.
             */
            while(true){
                String data = producer.produce();
                queue.add(data);
                System.out.println(name + " produced " + data);
                if(Thread.currentThread().isInterrupted()){
                    System.out.println("thread " + name + " interrupted.");
                    return;
                }
            }
        }
    }

    private static class Consumer implements Runnable{
        public void run(){
            String name = Thread.currentThread().getName();
            StringConsumer consumer = new SampleStringConsumer(100);


            try {
                while(true){
                    String data = queue.take();
                    consumer.consume(data);
                    System.out.println();
                }
            } catch (InterruptedException e) {
                System.out.println("thread " + name + " interrupted");
            }

        }
    }

    public static void main(String[] args) {
        Producer producer = new Producer();
        Thread producer1 = new Thread(producer);
        Thread producer2 = new Thread(producer);

        Consumer consumer = new Consumer();
        Thread consumer1 = new Thread(consumer);
        Thread consumer2 = new Thread(consumer);
        Thread consumer3 = new Thread(consumer);

        producer1.start();
        producer2.start();

        consumer1.start();
        consumer2.start();
        consumer3.start();

        try{
            Thread.sleep(10000);
        } catch (InterruptedException e){
            System.out.println("main thread interrupted");
        }

        producer1.interrupt();
        producer2.interrupt();

        consumer1.interrupt();
        consumer2.interrupt();
        consumer3.interrupt();


    }
}
