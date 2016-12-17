package nl.positor.modularity.classpath.impl;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.function.Predicate;

/**
 * If the filter returns true for a name, the class MUST be loadable by this classloader.
 */
public class FilteredClassLoader extends URLClassLoader {
    private final Predicate<String> filter;

    public FilteredClassLoader(URL[] classPath, ClassLoader parent, Predicate<String> filter) {
        super(classPath, parent);
        this.filter = filter;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (filter.test(name)) {
            return super.findClass(name);
        }
        throw new ClassNotFoundException();
    }

    public Predicate<String> getFilter() {
        return filter;
    }
}
