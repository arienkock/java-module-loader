package nl.positor.module.loading;

import com.google.common.collect.Iterables;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.function.Predicate;

/**
 * Created by Arien on 27-May-16.
 */
class FilteredClassLoader extends URLClassLoader {

    private Predicate<String> filter;

    FilteredClassLoader(ClassPath classPath, ClassLoader parent, Predicate<String> filter) {
        super(Arrays.copyOf(classPath.toArray(), Iterables.size(classPath)), parent);
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
