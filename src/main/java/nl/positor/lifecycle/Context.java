package nl.positor.lifecycle;

/**
 * Created by Arien on 26-May-16.
 */
@FunctionalInterface
public interface Context {
    Iterable<Lifecycle> getDependencies(Lifecycle lifecycleObject);
}
