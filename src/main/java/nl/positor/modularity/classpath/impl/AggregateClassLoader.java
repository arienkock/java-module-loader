package nl.positor.modularity.classpath.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;

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

    @Override
    public URL getResource(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected URL findResource(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Enumeration<URL> findResources(String name) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Package definePackage(String name, String specTitle, String specVersion, String specVendor, String implTitle, String implVersion, String implVendor, URL sealBase) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Package[] getPackages() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected String findLibrary(String libname) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDefaultAssertionStatus(boolean enabled) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPackageAssertionStatus(String packageName, boolean enabled) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setClassAssertionStatus(String className, boolean enabled) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearAssertionStatus() {
        throw new UnsupportedOperationException();
    }
}
