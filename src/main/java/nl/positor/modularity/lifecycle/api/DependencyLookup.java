package nl.positor.modularity.lifecycle.api;

import java.util.Collection;

/**
 * Created by Arien on 28-Dec-16.
 */
@FunctionalInterface
public interface DependencyLookup {
    Collection<? extends Lifecycle> getDependencies(Lifecycle lifecycleObject);
}
