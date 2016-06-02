package nl.positor.module.loading;

import com.google.common.collect.Iterables;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.function.Predicate;

/**
 * Created by Arien on 27-May-16.
 */
public class FilteredClassLoader extends URLClassLoader {

    private Predicate<String> filter;
    public FilteredClassLoader(Iterable<URL> urls, ClassLoader parent, Predicate<String> filter) {
        this(Iterables.toArray(urls, URL.class), parent, filter);
    }

    public FilteredClassLoader(URL[] urls, ClassLoader parent, Predicate<String> filter) {
        super(urls, parent);
        this.filter = filter;
    }
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (filter.test(name)) {
            super.findClass(name);
        }
        throw new ClassNotFoundException();
    }
}
