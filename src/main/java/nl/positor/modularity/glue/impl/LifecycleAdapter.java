package nl.positor.modularity.glue.impl;

import nl.positor.modularity.glue.api.Lifecycle;

/**
 * Created by Arien on 17-Dec-16.
 */
public class LifecycleAdapter implements nl.positor.modularity.lifecycle.api.Lifecycle {
    private final nl.positor.modularity.glue.api.Lifecycle delegate;

    public LifecycleAdapter(Lifecycle delegate) {
        this.delegate = delegate;
    }

    @Override
    public void start() {
        delegate.start();
    }

    @Override
    public void clean() {
        delegate.clean();
    }

    @Override
    public void stop() {
        delegate.stop();
    }
}
