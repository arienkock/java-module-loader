package nl.positor.module.loading;

import com.google.common.collect.Iterables;

import java.net.URL;

/**
 * Created by Arien on 10-Jun-16.
 */
public interface ClassPath extends Iterable<URL> {
    default URL[] toArray() {
        return Iterables.toArray(this, URL.class);
    };
}
