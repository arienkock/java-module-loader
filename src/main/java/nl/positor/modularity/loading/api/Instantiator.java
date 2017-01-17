package nl.positor.modularity.loading.api;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Arien on 16-Dec-16.
 */
@FunctionalInterface
public interface Instantiator {
    default Object create(ClassLoader classLoader) {
        return create(classLoader, list -> {});
    }

    Object create(ClassLoader classLoader, Consumer<List<Object>> referencedInstancesCallback);
}
