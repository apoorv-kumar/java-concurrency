package src.concurrency.barrier;

import javafx.scene.control.RadioMenuItem;
import org.omg.PortableServer.THREAD_POLICY_ID;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by apoorv on 4/28/17.
 */
public class Simulation {
    private static final int WORKER_COUNT = Runtime.getRuntime().availableProcessors();
    private static CyclicBarrier simulationCheckpoint = new CyclicBarrier(WORKER_COUNT, new CheckpointAction());
    private static int checkpointNum = 0;

    //increment checkpoint num and print
    //thread unsafe
    private static class CheckpointAction implements Runnable{
        @Override
        public void run() {
            System.out.printf("Reached checkpoint: %3d at %d ms\n", ++checkpointNum, System.currentTimeMillis()%1000000);
        }
    }

    private static class SimulationWorker implements Runnable{

        @Override
        public void run() {
            System.out.println("starting thread :" + Thread.currentThread().getName());
            Random rand = new Random();
            while(true){
                try{
                    Thread.sleep(rand.nextInt(500));
                    simulationCheckpoint.await(470, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e){
                    System.out.printf("worker %s interrupted. exit.\n", Thread.currentThread().getName());
                    break;
                } catch (BrokenBarrierException e) {
                    System.out.printf("worker %s: barrier broken. exit.\n", Thread.currentThread().getName());
                    break;
                } catch (TimeoutException e) {
                    System.out.printf("thread %s was timed out. Barrier will be broken.\n", Thread.currentThread().getName());
                }
            }
        }
    }

    public static void main(String[] args) {
        Thread[] workerThreads = new Thread[WORKER_COUNT];

        for (int i = 0; i < workerThreads.length; i++) {
            workerThreads[i] = new Thread(new SimulationWorker());
            workerThreads[i].start();
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //NOTE: this will break the barrier for some threads
        //so breaking barrier should handle
        for (Thread t: workerThreads) t.interrupt();


    }
}
