package nl.positor.modularity.glue.api;

import java.util.Collection;

/**
 * Created by Arien on 17-Dec-16.
 */
public interface Blueprint {
    Lifecycle create(ClassLoader classLoader);

    Collection<Component> getDependencies();
}
