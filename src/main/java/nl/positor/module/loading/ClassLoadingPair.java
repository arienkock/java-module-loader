package nl.positor.module.loading;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by Arien on 28-Mar-16.
 */
public class ClassLoadingPair {
    private final Function<ClassLoader, ? extends ClassLoader> privateClassLoaderSupplier;
    private final Function<ClassLoader, ? extends ClassLoader> publicClassLoaderSupplier;
    private final Supplier<ClassLoader> parentSupplier;
    private ClassLoader publicClassLoader;
    private ClassLoader privateClassLoader;

    public ClassLoadingPair(
            Supplier<ClassLoader> parentSupplier,
            Function<ClassLoader, ? extends ClassLoader> privateClassLoaderSupplier,
            Function<ClassLoader, ? extends ClassLoader> publicClassLoaderSupplier) {
        Objects.requireNonNull(publicClassLoaderSupplier);
        this.parentSupplier = parentSupplier;
        this.privateClassLoaderSupplier = privateClassLoaderSupplier;
        this.publicClassLoaderSupplier = publicClassLoaderSupplier;
    }

    public ClassLoader getPublicClassLoader() {
        if (publicClassLoader == null) {
            publicClassLoader = parentSupplier == null ?
                    Thread.currentThread().getContextClassLoader() :
                    publicClassLoaderSupplier.apply(parentSupplier.get());
        }
        return publicClassLoader;
    }

    public ClassLoader getPrivateClassLoader() {
        if (privateClassLoader == null) {
            privateClassLoader = privateClassLoaderSupplier == null ?
                    getPublicClassLoader() : privateClassLoaderSupplier.apply(getPublicClassLoader());
        }
        return privateClassLoader;
    }

    public static Function<ClassLoader, ? extends ClassLoader> urlClassloaderProvider(URL[] classpath) {
        return parentClassloader -> new URLClassLoader(classpath, parentClassloader);
    }

    public static Supplier<ClassLoader> joinPublic(Collection<ClassLoadingPair> loaders) {
        return () -> new AggregateClassLoader(
                loaders
                    .stream()
                    .map(ClassLoadingPair::getPublicClassLoader)
                    .collect(Collectors.toList()));
    }
}
