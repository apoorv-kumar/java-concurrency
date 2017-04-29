package src.concurrency.basic;

/* ideally you'd want consumer to consume a Consumable */
public interface StringConsumer {
    void consume(String data);
}
