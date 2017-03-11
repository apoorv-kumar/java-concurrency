package src.concurrency.playground;

/**
 * Created by apoorv on 11/3/17.
 */
public class TestInterruption implements Runnable{

    public static void main(String[] args) {
        Thread th = new Thread(new TestInterruption());
        th.start();
        try{
            Thread.sleep(5000);
        } catch (InterruptedException ex){
            System.out.println(Thread.currentThread().getName() + " interrupted");
        }

        System.out.println(Thread.currentThread().getName() + " Interrupting " + th.getName());
        th.interrupt();

    }

    public void run(){
        try{
            while(true){
                System.out.println( Thread.currentThread().getName() + " running...");
                Thread.sleep(2000);
            }

        } catch (InterruptedException e){
            System.out.println(Thread.currentThread().getName() + " Interrupted mid sleep. Closing thread");
        }
    }
}
