package nl.positor.lifecycle;

/**
 * Created by Arien on 26-May-16.
 */
public interface Lifecycle {
    /**
     * If already started, then no action should be taken.
     */
    void start();
    // clean up used resources
    void clean();
    void stop();
}
