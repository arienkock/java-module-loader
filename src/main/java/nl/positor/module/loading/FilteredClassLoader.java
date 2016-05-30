package nl.positor.module.loading;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.function.Predicate;

/**
 * Created by Arien on 27-May-16.
 */
public class FilteredClassLoader extends URLClassLoader {

    private Predicate<String> filter;

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
