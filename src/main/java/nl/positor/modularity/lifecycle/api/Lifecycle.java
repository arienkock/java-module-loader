package nl.positor.modularity.lifecycle.api;

/**
 * Created by Arien on 26-May-16.
 */
public interface Lifecycle {
    boolean startIfStopped();

    void clean();

    boolean stopIfStarted();
}
