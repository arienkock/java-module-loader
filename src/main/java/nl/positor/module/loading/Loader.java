package nl.positor.module.loading;

import nl.positor.module.definition.ModuleClassPath;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by Arien on 30-May-16.
 */
public class Loader {

    public static Object load(ModuleClassPath moduleClassPath,
                              ClassLoader suppliedParentClassLoader,
                              Consumer<LoadedModuleInfo> callback)
            throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return load(moduleClassPath, suppliedParentClassLoader, Instantiator.ZERO_ARG_CONSTRUCTOR_INSTANTIATOR, callback);
    }

    public static Object load(ModuleClassPath moduleClassPath,
                              ClassLoader suppliedParentClassLoader,
                              Instantiator instantiator,
                              Consumer<LoadedModuleInfo> callback)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {
        ClassLoader[] classLoaders = classLoadersFor(suppliedParentClassLoader, moduleClassPath);
        ClassLoader publicLoader = classLoaders[0];
        ClassLoader privateLoader = classLoaders[1];
        final Object instance;
        String entryPointClassName = moduleClassPath.getDefinition().getEntryPoint();
        if (instantiator != null && entryPointClassName != null) {
            Class<?> entryPointClass = privateLoader.loadClass(entryPointClassName);
            instance = instantiator.instantiate(entryPointClass);
        } else {
            instance = null;
        }
        InnerLoadedModuleInfo info = new InnerLoadedModuleInfo(moduleClassPath, publicLoader, privateLoader, instance);
        callback.accept(info);
        return info;
    }

    private static ClassLoader firstOf(ClassLoader suppliedParentClassLoader, ClassLoader contextClassLoader, ClassLoader classLoader) {
        if (suppliedParentClassLoader != null) {
            return suppliedParentClassLoader;
        }
        if (contextClassLoader != null) {
            return contextClassLoader;
        }
        if (classLoader != classLoader) {
            return classLoader;
        }
        return null;
    }

    public interface Instantiator {
        Instantiator ZERO_ARG_CONSTRUCTOR_INSTANTIATOR =
                c -> {
                    Constructor<?>[] constructors = c.getConstructors();
                    for (Constructor<?> constructor : constructors) {
                        if (constructor.getGenericParameterTypes().length == 0) {
                            return constructor.newInstance();
                        }
                    }
                    throw new IllegalArgumentException("No zero-arg constructor found in class " + c.getName());
                };
        Object instantiate(Class<?> clazz) throws IllegalAccessException, InvocationTargetException, InstantiationException;

    }

    public interface LoadedModuleInfo {
        ModuleClassPath getModuleClassPath();
        ClassLoader getPublicClassLoader();
        ClassLoader getPrivateClassLoader();
        Object getInstance();
    }

    private static ClassLoader[] classLoadersFor(ClassLoader suppliedParentClassLoader, ModuleClassPath classPath) {
        ClassLoader parentClassLoader = firstOf(
                suppliedParentClassLoader,
                Thread.currentThread().getContextClassLoader(),
                System.class.getClassLoader());
        Predicate<String> isPublicClass = classPath.getDefinition().getPublicClasses()::contains;
        FilteredClassLoader publicLoader =
                new FilteredClassLoader(classPath.getClassPath(), parentClassLoader, isPublicClass);
        FilteredClassLoader privateLoader =
                new FilteredClassLoader(classPath.getClassPath(), publicLoader, isPublicClass.negate());
        return new ClassLoader[] { publicLoader, privateLoader };
    }

    private static class InnerLoadedModuleInfo implements LoadedModuleInfo {
        private final ModuleClassPath moduleClassPath;
        private final ClassLoader publicLoader;
        private final ClassLoader privateLoader;
        private final Object instance;

        public InnerLoadedModuleInfo(ModuleClassPath moduleClassPath, ClassLoader publicLoader, ClassLoader privateLoader, Object instance) {
            this.moduleClassPath = moduleClassPath;
            this.publicLoader = publicLoader;
            this.privateLoader = privateLoader;
            this.instance = instance;
        }

        @Override
        public ModuleClassPath getModuleClassPath() {
            return moduleClassPath;
        }

        @Override
        public ClassLoader getPublicClassLoader() {
            return publicLoader;
        }

        @Override
        public ClassLoader getPrivateClassLoader() {
            return privateLoader;
        }

        @Override
        public Object getInstance() {
            return instance;
        }
    }
}
