package nl.positor.module.loading;

import com.google.common.collect.Iterables;
import nl.positor.module.definition.ModuleClassPath;

import java.net.URL;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created by Arien on 27-May-16.
 */
public class ClassReader {
    public static ModuleClassReader classReaderFrom(ModuleClassPath classPath, Supplier<ClassLoader> parentSupplier) {
        return new ModuleClassReader() {
            private ClassLoader publicClassloader;
            private ClassLoader privateClassloader;
            private Predicate<String> filter = classPath.getDefinition().getPublicClasses()::contains;

            @Override
            public ClassLoader getPublicClassLoader() {
                if (publicClassloader == null) {
                    publicClassloader = new FilteredClassLoader(
                            Iterables.toArray(classPath.getClassPath(), URL.class),
                            parentSupplier.get(),
                            filter);
                }
                return publicClassloader;
            }

            public ClassLoader getPrivateClassLoader() {
                if (privateClassloader == null) {
                    privateClassloader = new FilteredClassLoader(
                            Iterables.toArray(classPath.getClassPath(), URL.class),
                            getPublicClassLoader(),
                            filter.negate());
                }
                return privateClassloader;
            }

            @Override
            public Class<?> loadPrivate(String name) throws ClassNotFoundException {
                return getPrivateClassLoader().loadClass(name);
            }
        };
    }
}
