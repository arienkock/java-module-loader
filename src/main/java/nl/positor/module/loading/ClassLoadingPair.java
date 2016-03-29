package nl.positor.module.loading;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Arien on 28-Mar-16.
 */
public class ClassLoadingPair {
    private final Function<ClassLoader, ? extends ClassLoader> privateClassLoaderSupplier;
    private final Function<ClassLoader, ? extends ClassLoader> publicClassLoaderSupplier;
    private final Supplier<ClassLoader> parentProvider;
    private ClassLoader publicClassLoader;
    private ClassLoader privateClassLoader;

    public ClassLoadingPair(
            Supplier<ClassLoader> parentProvider,
            Function<ClassLoader, ? extends ClassLoader> privateClassLoaderSupplier,
            Function<ClassLoader, ? extends ClassLoader> publicClassLoaderSupplier) {
        this.parentProvider = parentProvider;
        this.privateClassLoaderSupplier = privateClassLoaderSupplier;
        this.publicClassLoaderSupplier = publicClassLoaderSupplier;
    }

    public ClassLoader getPublicClassLoader() {
        if (publicClassLoader == null) {
            publicClassLoader = publicClassLoaderSupplier.apply(parentProvider.get());
        }
        return publicClassLoader;
    }

    public ClassLoader getPrivateClassLoader() {
        if (privateClassLoader == null && privateClassLoaderSupplier != null) {
            privateClassLoader = privateClassLoaderSupplier.apply(getPublicClassLoader());
        }
        return privateClassLoader;
    }
}
