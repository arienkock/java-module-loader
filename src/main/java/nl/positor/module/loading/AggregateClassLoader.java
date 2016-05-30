package nl.positor.module.loading;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

/**
 * Created by Arien on 25-May-16.
 */
public class AggregateClassLoader extends ClassLoader {
    Collection<ClassLoader> _delegates;

    public AggregateClassLoader(Collection<ClassLoader> delegates) {
        if (delegates.isEmpty()) {
            throw new IllegalArgumentException("Delegate classloaders must exist.");
        }
        this._delegates = delegates;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return loadClass(name, false);
    }

    @Override
    public URL getResource(String name) {
        for (ClassLoader cl : _delegates) {
            URL url = cl.getResource(name);
            if (url != null) {
                return url;
            }
        }
        return null;
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        Set<URL> urlSet = new HashSet<>();
        for (ClassLoader cl : _delegates) {
            Enumeration<URL> resources = cl.getResources(name);
            while (resources.hasMoreElements()) {
                urlSet.add(resources.nextElement());
            }
        }
        return new Vector<URL>(urlSet).elements();
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        for (ClassLoader cl : _delegates) {
            InputStream result = cl.getResourceAsStream(name);
            if (result != null) {
                return result;
            }
        }
        return null;
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

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        for (ClassLoader cl : _delegates) {
            try {
                return cl.loadClass(name);
            } catch (ClassNotFoundException e) {
            }
        }
        throw new ClassNotFoundException();
    }

    @Override
    protected Object getClassLoadingLock(String className) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
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
    protected Package definePackage(String name, String specTitle, String specVersion, String specVendor, String implTitle, String implVersion, String implVendor, URL sealBase) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Package getPackage(String name) {
        return null;
    }

    @Override
    protected Package[] getPackages() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected String findLibrary(String libname) {
        throw new UnsupportedOperationException();
    }
}
