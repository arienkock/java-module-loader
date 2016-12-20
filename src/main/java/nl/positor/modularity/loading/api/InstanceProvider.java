package nl.positor.modularity.loading.api;

/**
 * Created by Arien on 20-Dec-16.
 */
@FunctionalInterface
public interface InstanceProvider {
    Object get();
}
