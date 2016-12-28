package nl.positor.modularity.lifecycle.api;

/**
 * Created by Arien on 26-May-16.
 */
public interface Restarter {
    void restart(ReverseDependencyLookup context, Lifecycle... lifecycleObjects);

    void stop(ReverseDependencyLookup context, Lifecycle... objectsToStop);

    void start(DependencyLookup context, Lifecycle... objectsToStart);
}
