package nl.positor.module.loading;

import java.util.function.Supplier;

/**
 * Created by Arien on 10-Jun-16.
 */

@FunctionalInterface
public interface InstantiatorFactory {
    Instantiator forClassName(CharSequence className);

}
