package nl.positor.modularity.glue.testcase;

import java.util.function.Consumer;

/**
 * Created by Arien on 21-Dec-16.
 */
public class MyHandler {
    private final MessageServer server;
    private final MessageDatabase messageDatabase;
    private final Consumer<String> consumer;

    public MyHandler(MessageServer server, MessageDatabase messageDatabase) {
        this.server = server;
        this.messageDatabase = messageDatabase;
        this.consumer = this::handleMessage;
    }

    public synchronized void start() {
        server.subscribe(consumer);
    }

    public synchronized void stop() {
        server.unsubscribe();
    }

    public synchronized void handleMessage(String message) {
        messageDatabase.save(message);
    }
}
