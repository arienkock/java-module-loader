package nl.positor.modularity.glue.testcase;

import java.util.function.Consumer;

/**
 * Created by Arien on 21-Dec-16.
 */
public class MyHandler {
    private final MessageServer server;
    private final MessageDatabase messageDatabase;
    private final Consumer<String> consumer;
    private boolean started = false;
    private int latch = 0;

    public MyHandler(MessageServer server, MessageDatabase messageDatabase) {
        this.server = server;
        this.messageDatabase = messageDatabase;
        this.consumer = this::handleMessage;
    }

    public synchronized void start() {
        if (!started) {
            server.subscribe(consumer);
            started = true;
        }
    }

    public synchronized void stop() {
        if (started) {
            server.unsubscribe(consumer);
            started = false;
            while (latch != 0) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public synchronized void handleMessage(String message) {
        latch++;
        try {
            messageDatabase.save(message);
        } finally {
            if (--latch == 0) {
                this.notify();
            }
        }
    }
}
