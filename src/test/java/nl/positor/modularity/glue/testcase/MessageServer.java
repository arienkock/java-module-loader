package nl.positor.modularity.glue.testcase;

import java.util.function.Consumer;

/**
 * Created by Arien on 21-Dec-16.
 */
public interface MessageServer {
    void acceptMessage(String message);

    void subscribe(Consumer<String> messageConsumer);

    void unsubscribe();
}
