package nl.positor.modularity.lifecycle.api;

/**
 * Created by Arien on 26-May-16.
 */
@FunctionalInterface
public interface Context {
    Iterable<Lifecycle> getDependants(Lifecycle lifecycleObject);
}
