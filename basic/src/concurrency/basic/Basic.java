package src.concurrency.basic;

class TestDoubleCounter{
    //invariant check() == true
    private long a1 = 1;
    private long a2 = 1;


    // Lock is an interface in Java. There are multiple custom implementations.
    // For this use case, we can use the monitor lock found in all Java objects.
    private final Object counter_lock = new Object();


    // simulates a long running thread
    // Could've run in parallel with other
    // methods
    // This can be used to see how inadvertent
    // internal locking can slow things down.
    // EDIT: Results show no such thing.
    // Compiler probably optimized the hell out of this.
    // EDIT: Indeed. z is not used. So the compiler just
    // removed it and put a boolean assignment in loop.
    synchronized void dummy_slow_routine_internal_lock(){
        for(int i = 0; i < 1000000; i++){
            int z = 1000%7;
        }
    }
    
    void dummy_slow_routine_lock_free(){
        for(int i = 0; i < 1000000; i++){
            int z = 1000%7;
        }
    }

    void invariant_check_explicit_lock(){
        synchronized (counter_lock){
            assert(a1 == a2);
        }
    }

    synchronized void invariant_check_internal_lock(){
        assert(a1 == a2);
    }

    synchronized void increment_internal_lock(){
        a1 += 1;
        a2 += 1;
    }

    synchronized void decrement_internal_lock(){
        a1 += -1;
        a2 += -1;
    }

    void increment_explicit_lock(){
        synchronized (counter_lock) {
            a1 += 1;
            a2 += 1;
        }
    }

    void decrement_explicit_lock(){
        synchronized (counter_lock) {
            a1 -= 1;
            a2 -= 1;
        }
    }
}

enum ExecType{
    SLOW_ROUTINE_INTERNAL_LOCK,
    SLOW_ROUTINE_LOCK_FREE,
    INVARIANT_EXPLICIT_LOCK,
    INVARIANT_INTERNAL_LOCK,
    INCREMENT_INTERNAL_LOCK,
    DECREMENT_INTERNAL_LOCK,
    INCREMENT_EXPLICIT_LOCK,
    DECREMENT_EXPLICIT_LOCK
}

class AsycDoubleCounterTester extends Thread{
    private final TestDoubleCounter dc;
    private final ExecType execType;
    private final int cycles;
    public AsycDoubleCounterTester(
            TestDoubleCounter dc,
            ExecType execType,
            int cycles
    ){
        this.cycles = cycles;
        this.dc = dc;
        this.execType = execType;
    }

    private void loopLambda(Runnable lambda){
        for (int i = 0; i < cycles; i++) {
            lambda.run();
        }
    }

    @Override
    public void run(){

        switch (execType){
            case INCREMENT_EXPLICIT_LOCK: loopLambda(dc::increment_explicit_lock);
            break;
            case DECREMENT_EXPLICIT_LOCK: loopLambda(dc::decrement_explicit_lock);
            break;
            case INCREMENT_INTERNAL_LOCK: loopLambda(dc::increment_internal_lock);
            break;
            case DECREMENT_INTERNAL_LOCK: loopLambda(dc::decrement_internal_lock);
            break;
            case INVARIANT_INTERNAL_LOCK: loopLambda(dc::invariant_check_internal_lock);
            break;
            case INVARIANT_EXPLICIT_LOCK: loopLambda(dc::invariant_check_explicit_lock);
            break;
            case SLOW_ROUTINE_INTERNAL_LOCK: loopLambda(dc::dummy_slow_routine_internal_lock);
            break;
            case SLOW_ROUTINE_LOCK_FREE: loopLambda(dc::dummy_slow_routine_lock_free);
            break;
        }

    }
}

class Basic {

    private static void simpleExplicitLockTest (int cycles, TestDoubleCounter dc) throws InterruptedException{
        //make locked increments, locked decrements and locked checks.
        AsycDoubleCounterTester tester_inc = new AsycDoubleCounterTester(
                dc,
                ExecType.INCREMENT_EXPLICIT_LOCK,
                cycles);
        AsycDoubleCounterTester tester_dec = new AsycDoubleCounterTester(
                dc,
                ExecType.DECREMENT_EXPLICIT_LOCK,
                cycles);
        AsycDoubleCounterTester tester_invariant = new AsycDoubleCounterTester(
                dc,
                ExecType.INVARIANT_EXPLICIT_LOCK,
                cycles);


        tester_inc.start();
        tester_dec.start();
        tester_invariant.start();


        /* running threads are considered 'GC roots' and thus always referenced
           which makes them immune to GC. After completion they're no longer roots.

           So we don't necessarily need to join the threads above, but let's do it
           so we can time it.
         */

        tester_inc.join();
        tester_dec.join();
        tester_invariant.join();
    }

    private static void simpleInternalLockTest (int cycles, TestDoubleCounter dc) throws InterruptedException {
        //make locked increments, locked decrements and locked checks.
        AsycDoubleCounterTester tester_inc = new AsycDoubleCounterTester(
                dc,
                ExecType.INCREMENT_INTERNAL_LOCK,
                cycles);
        AsycDoubleCounterTester tester_dec = new AsycDoubleCounterTester(
                dc,
                ExecType.DECREMENT_INTERNAL_LOCK,
                cycles);
        AsycDoubleCounterTester tester_invariant = new AsycDoubleCounterTester(
                dc,
                ExecType.INVARIANT_INTERNAL_LOCK,
                cycles);


        tester_inc.start();
        tester_dec.start();
        tester_invariant.start();

        tester_inc.join();
        tester_dec.join();
        tester_invariant.join();
    }

    private static void slowFuncInternalLockTest (int cycles, TestDoubleCounter dc) throws InterruptedException {
        //make locked increments, locked decrements and locked checks.
        AsycDoubleCounterTester tester_inc = new AsycDoubleCounterTester(
                dc,
                ExecType.INCREMENT_INTERNAL_LOCK,
                cycles);
        AsycDoubleCounterTester tester_dec = new AsycDoubleCounterTester(
                dc,
                ExecType.DECREMENT_INTERNAL_LOCK,
                cycles);
        AsycDoubleCounterTester tester_invariant = new AsycDoubleCounterTester(
                dc,
                ExecType.INVARIANT_INTERNAL_LOCK,
                cycles);
        AsycDoubleCounterTester tester_slow_routine = new AsycDoubleCounterTester(
                dc,
                ExecType.SLOW_ROUTINE_INTERNAL_LOCK,
                cycles);

        tester_slow_routine.start();
        tester_inc.start();
        tester_dec.start();
        tester_invariant.start();

        tester_slow_routine.join();
        tester_inc.join();
        tester_dec.join();
        tester_invariant.join();
    }

    private static void slowFuncLockFreeTest (int cycles, TestDoubleCounter dc) throws InterruptedException {
        //make locked increments, locked decrements and locked checks.
        AsycDoubleCounterTester tester_inc = new AsycDoubleCounterTester(
                dc,
                ExecType.INCREMENT_EXPLICIT_LOCK,
                cycles);
        AsycDoubleCounterTester tester_dec = new AsycDoubleCounterTester(
                dc,
                ExecType.DECREMENT_EXPLICIT_LOCK,
                cycles);
        AsycDoubleCounterTester tester_invariant = new AsycDoubleCounterTester(
                dc,
                ExecType.INVARIANT_EXPLICIT_LOCK,
                cycles);
        AsycDoubleCounterTester tester_slow_routine = new AsycDoubleCounterTester(
                dc,
                ExecType.SLOW_ROUTINE_LOCK_FREE,
                cycles);

        tester_slow_routine.start();
        tester_inc.start();
        tester_dec.start();
        tester_invariant.start();

        tester_slow_routine.join();
        tester_inc.join();
        tester_dec.join();
        tester_invariant.join();
    }

    public static void main(String[] args) {
        TestDoubleCounter dc = new TestDoubleCounter();
        
        int cycles = 100000;
        try {

            long start_time = System.nanoTime();
            simpleExplicitLockTest(cycles, dc);
            long end_time = System.nanoTime();

            System.out.printf("Time for explicit lock - %d us\n", (end_time - start_time)/1000);

            //TODO: There seems to be some warmup time associated with the locks. Why ?

            start_time = System.nanoTime();
            simpleExplicitLockTest(cycles, dc);
            end_time = System.nanoTime();

            System.out.printf("Time for explicit lock (run 2) - %d us\n", (end_time - start_time)/1000);

            start_time = System.nanoTime();
            simpleInternalLockTest(cycles, dc);
            end_time = System.nanoTime();

            System.out.printf("Time for internal lock - %d us\n", (end_time - start_time)/1000);

            start_time = System.nanoTime();
            simpleInternalLockTest(cycles, dc);
            end_time = System.nanoTime();

            System.out.printf("Time for internal lock (run 2)- %d us\n", (end_time - start_time)/1000);

            start_time = System.nanoTime();
            slowFuncInternalLockTest(cycles, dc);
            end_time = System.nanoTime();

            System.out.printf("Time for internal lock with slow func- %d us\n", (end_time - start_time)/1000);

            start_time = System.nanoTime();
            slowFuncLockFreeTest(cycles, dc);
            end_time = System.nanoTime();

            System.out.printf("Time for explicit lock with lock free slow func- %d us\n", (end_time - start_time)/1000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
