package nl.positor.module.wiring;

import java.util.function.Function;

/**
 * Created by Arien on 26-May-16.
 */
@FunctionalInterface
public interface Wiring {
    Object apply(Object target, Function<String, Object> dependencyLookupMap);
}
