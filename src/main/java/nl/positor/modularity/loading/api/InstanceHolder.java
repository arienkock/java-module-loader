package nl.positor.modularity.loading.api;

/**
 * Created by Arien on 16-Dec-16.
 */
public interface InstanceHolder {
    Object get();

    void release();
}
