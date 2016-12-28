package nl.positor.modularity.glue.testcase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Created by Arien on 21-Dec-16.
 */
public class MessageServerExample implements MessageServer {
    private AtomicReference<Consumer<String>> consumer = new AtomicReference<>();
    private ExecutorService executor;

    public void start() {
        this.executor = Executors.newFixedThreadPool(4);
        this.executor.execute(this::randomMessages);
    }

    private void randomMessages() {
        while (true) {
            this.consumer.get().accept(Integer.toHexString((int) (Math.random() * 100_000)));
        }
    }

    public void stop() throws InterruptedException {
        this.executor.shutdownNow();
    }

    @Override
    public void subscribe(Consumer<String> messageConsumer) {
        if (!consumer.compareAndSet(null, messageConsumer)) {
            throw new Error("Multiple registrations");
        }
    }

    @Override
    public void unsubscribe(Consumer<String> messageConsumer) {
        consumer.set(null);
    }
}
