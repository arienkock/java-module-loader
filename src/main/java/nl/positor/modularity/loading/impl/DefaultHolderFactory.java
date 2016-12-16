package nl.positor.modularity.loading.impl;

import nl.positor.modularity.loading.api.HolderFactory;
import nl.positor.modularity.loading.api.InstanceHolder;
import nl.positor.modularity.loading.api.Instantiator;

/**
 * Created by Arien on 16-Dec-16.
 */
public class DefaultHolderFactory implements HolderFactory {
    @Override
    public InstanceHolder createHolder(Instantiator instantiator, ClassLoader classLoader) {
        return new AtomicHolder(instantiator, classLoader);
    }
}
