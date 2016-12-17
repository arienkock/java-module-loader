package nl.positor.modularity.classpath.impl;

import java.util.Arrays;

/**
 * Created by Arien on 16-Dec-16.
 */
public class AggregateClassLoader extends ClassLoader {
    private final FilteredClassLoader[] delegates;

    public AggregateClassLoader(ClassLoader parent, FilteredClassLoader... delegates) {
        super(parent);
        this.delegates = delegates;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        for (FilteredClassLoader delegate : delegates) {
            if (delegate.getFilter().test(name)) {
                try {
                    return delegate.loadClass(name);
                } catch (ClassNotFoundException e) {
                    new RuntimeException("Classloader lied about being able to load class with name " + name + " from classpath " + Arrays.toString(delegate.getURLs()), e);
                }
            }
        }
        throw new ClassNotFoundException(name);
    }
}
