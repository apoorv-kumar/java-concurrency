package src.concurrency.playground;

/**
 * Created by apoorv on 25/2/17.
 */
public class TestVisibility {
    private static boolean ready;
    private static int number;

    private static class ReaderThread extends Thread {
        public void run(){
            while(!ready)
                Thread.yield();
            //won't execute until ready is true
            System.out.printf("Read value: %d", number);
        }
    }

    public static void main(String[] args) {
        Thread t = new ReaderThread();
        t.start();
        //see if variables actually reflect
        //EDIT: it seems they do get reflected during my execs
        //but still no formal guarantees around it.
        number = 42;
        ready = true;
    }
}
