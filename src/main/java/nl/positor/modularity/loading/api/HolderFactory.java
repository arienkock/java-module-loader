package nl.positor.modularity.loading.api;

/**
 * Created by Arien on 16-Dec-16.
 */
@FunctionalInterface
public interface HolderFactory {
    InstanceHolder createHolder(Instantiator instantiator, ClassLoader classLoader);
}
