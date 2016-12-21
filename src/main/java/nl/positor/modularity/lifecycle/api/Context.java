package nl.positor.modularity.lifecycle.api;

import java.util.Collection;

/**
 * Created by Arien on 26-May-16.
 */
@FunctionalInterface
public interface Context {
    Collection<Lifecycle> getDependants(Lifecycle lifecycleObject);
}
