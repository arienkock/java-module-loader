package nl.positor.modularity.loading;

import nl.positor.modularity.loading.api.InstanceProvider;

/**
 * Created by Arien on 16-Dec-16.
 */
public class DummyHolder implements InstanceProvider {
    private Object instance;

    public DummyHolder(Object instance) {
        this.instance = instance;
    }

    @Override
    public Object get() {
        return instance;
    }

}
