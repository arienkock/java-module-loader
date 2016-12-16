package nl.positor.module.loading;

import com.google.common.collect.Iterables;

/**
 * Created by Arien on 25-May-16.
 */
class AggregateClassLoader extends ClassLoader {
    private final Iterable<ClassLoader> delegates;

    AggregateClassLoader(Iterable<ClassLoader> delegates) {
        if (delegates == null || Iterables.isEmpty(delegates)) {
            throw new IllegalArgumentException("Delegate classloaders must exist.");
        }
        this.delegates = delegates;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }

    Iterable<ClassLoader> getDelegates() {
        return delegates;
    }
}
