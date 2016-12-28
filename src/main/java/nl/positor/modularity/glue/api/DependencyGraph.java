package nl.positor.modularity.glue.api;

import nl.positor.modularity.lifecycle.api.ReverseDependencyLookup;

/**
 * Created by Arien on 17-Dec-16.
 */
public interface DependencyGraph extends ReverseDependencyLookup {

    void startAll();
}
