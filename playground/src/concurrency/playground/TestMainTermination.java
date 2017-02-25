package src.concurrency.playground;

/**
 * Created by apoorv on 25/2/17.
 */
class TestMainTermination extends Thread {
    public void run() {
        try{
            //see if it ever wakes up
            Thread.sleep(1000);
        } catch (InterruptedException e){
            System.out.println("insomnia!");
        }
        System.out.println("explicit thread complete...");
    }
    public static void main(String args[]){
        TestMainTermination t1=new TestMainTermination();
        t1.start();
        System.out.println("Reached of main");

        // Code will wait here.

        //The application won't exit until either all
        // theads return/die or an explicit runtime.exit
        // is called.

        //Runtime.getRuntime().exit(1);
    }
}

