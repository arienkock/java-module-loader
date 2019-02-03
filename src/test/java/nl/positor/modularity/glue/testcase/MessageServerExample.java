package nl.positor.modularity.glue.testcase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Created by Arien on 21-Dec-16.
 */
public class MessageServerExample implements MessageServer {
    private AtomicReference<Consumer<String>> consumer = new AtomicReference<>();
    private AtomicLong counter;

    public void init() {
        this.counter = new AtomicLong();
    }

    public void stop() throws InterruptedException {
        consumer.set(null);
    }

    public AtomicLong getCounter() {
        return counter;
    }

    @Override
    public void acceptMessage(String message) {
        counter.incrementAndGet();
        consumer.get().accept(message);
    }

    @Override
    public void subscribe(Consumer<String> messageConsumer) {
        if (!consumer.compareAndSet(null, messageConsumer)) {
            throw new Error("Multiple registrations");
        }
    }

    @Override
    public void unsubscribe() {
        consumer.set(null);
    }
}
