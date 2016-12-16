package nl.positor.modularity.loading.impl;

import nl.positor.modularity.loading.api.InstanceHolder;
import nl.positor.modularity.loading.api.Instantiator;

/**
 * Created by Arien on 16-Dec-16.
 */
public class AtomicHolder implements InstanceHolder {
    private final Instantiator instantiator;
    private final ClassLoader classLoader;
    private Object instance;

    public AtomicHolder(Instantiator instantiator, ClassLoader classLoader) {
        this.instantiator = instantiator;
        this.classLoader = classLoader;
    }

    @Override
    public synchronized Object get() {
        if (instance == null) {
            instance = instantiator.create(classLoader);
        }
        return instance;
    }

    @Override
    public synchronized void release() {
        instance = null;
    }
}
