package nl.positor.modularity.glue.api;

/**
 * Created by Arien on 26-May-16.
 */
public interface Lifecycle {
    void start();

    void clean();

    void stop();
}
