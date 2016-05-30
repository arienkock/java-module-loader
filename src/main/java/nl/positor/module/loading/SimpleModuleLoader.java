package nl.positor.module.loading;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Lazily instantiates an object of the type specified by {@link #className} and
 * keeps a reference to it when created through {@link #get()}. The reference
 * making it a singleton within the scope of a loader instance until the
 * reference is unset using the {@link #reload()} methods. Delegates the creation of and behavior
 * of {@ling ClassLoader}s to supplier objects provided during instantiation.
 *
 * A module may only have public classes (i.e. no privateClassLoaderSupplier is supplied).
 * However, it's recommended to only expose the minimum set of types required to use/compose with the module.
 *
 * @author Arien
 *
 */
public class SimpleModuleLoader implements ModuleLoader, ReloadListener {
	private final String className;
    private final Supplier<ClassLoadingPair> classLoadingSource;
    private Object moduleInstance;
    private ClassLoadingPair classLoadingPair;
	private Set<ReloadListener> reloadListeners = new HashSet<>();

	/**
	 * Creates a new instance. Use {@link ModuleLoaderBuilder} for convenience.
	 *
	 * @param className Qualified name of the module's implementing class.
	 * @param classLoadingSource The actual class loading proider
	 */
	public SimpleModuleLoader(String className, Supplier<ClassLoadingPair> classLoadingSource, Iterable<ModuleLoader> loaders) {
		this.className = className;
        this.classLoadingSource = classLoadingSource;
	}

    ClassLoadingPair getClassLoadingPair() {
        if (classLoadingPair == null) {
            classLoadingPair = classLoadingSource.get();
        }
        return classLoadingPair;
    }

	/**
	 * Loads a new instance of the module with the currently active
	 * {@link ClassLoader}s without associating it with this loader. Use
	 * {@link #get()} if you want to create a singleton (within the scope of
	 * this loader) module.
     *
     * Instantiates the module class using its zero-argument constructor, through
     * reflection as recommended by <a href="https://docs.oracle.com/javase/tutorial/reflect/member/ctorInstance.html">
     * this Oracle tutorial</a>.
	 *
	 * @return a new module
	 * @throws ClassNotFoundException propagated loaderFrom inner classloaders
	 * @throws InstantiationException propagated loaderFrom inner classloaders
	 * @throws IllegalAccessException propagated loaderFrom inner classloaders
     * @throws InvocationTargetException when {@link Constructor#newInstance} throws an exception.
	 */
	Object load() throws
            ClassNotFoundException,
            InstantiationException,
            IllegalAccessException,
            InvocationTargetException {
        ClassLoadingPair pair = getClassLoadingPair();
        Class<?> moduleClass = pair.getPrivateClassLoader().loadClass(className);
        Constructor<?>[] ctors = moduleClass.getConstructors();
        Constructor ctor = null;
        for (int i = 0; i < ctors.length; i++) {
            ctor = ctors[i];
            if (ctor.getGenericParameterTypes().length == 0) {
				moduleInstance = ctor.newInstance();
				return moduleInstance;
			}
        }
		throw new IllegalArgumentException("No zero-arg constructor exists");
	}

	/**
	 * Lazily initializes a new singleton module if an entry-point className was provided.
	 * Subsequent calls return the same instance until one of the {@link #reload()} methods is called.
	 *
	 * @return the current module instance
	 * @throws ClassNotFoundException propagated loaderFrom inner classloaders
	 * @throws InstantiationException propagated loaderFrom inner classloaders
	 * @throws IllegalAccessException propagated loaderFrom inner classloaders
     * @throws InvocationTargetException propagated loaderFrom {@ling #load}
	 */
	public Object get() throws
            ClassNotFoundException,
            InstantiationException,
            IllegalAccessException,
            InvocationTargetException {
		if (className != null) {
			if (moduleInstance == null) {
				moduleInstance = load();
			}
			return moduleInstance;
		}
		return null;
	}

    /**
     * Unreferences the module instance and both classloaders,
     * then calls {@link #get()} resulting in a new module instance.
     *
     * @return The new singleton instance
	 * @throws ClassNotFoundException propagated loaderFrom inner classloaders
	 * @throws InstantiationException propagated loaderFrom inner classloaders
	 * @throws IllegalAccessException propagated loaderFrom inner classloaders
     * @throws InvocationTargetException propagated loaderFrom {@link #load}
	 */
	public Object reload() throws
            ClassNotFoundException,
            InstantiationException,
            IllegalAccessException,
            InvocationTargetException {
		clear();
		afterReload();
		return get();
	}

	private void clear() throws ClassNotFoundException,
			InstantiationException,
			IllegalAccessException,
			InvocationTargetException {
		Object oldInstance = moduleInstance;
		for (ReloadListener listener : reloadListeners) {
			try {
				listener.beforeReload(oldInstance);
			} catch (ClassNotFoundException |
					InstantiationException |
					IllegalAccessException |
					InvocationTargetException e) {
				throw e;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		classLoadingPair = null;
		moduleInstance = null;
	}

	@Override
	public void addReloadListener(ReloadListener listener) {
		reloadListeners.add(listener);
	}

	@Override
	public void beforeReload(Object previousInstance) throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
		clear();
	}

	@Override
	public void afterReload() throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
		for (ReloadListener listener : reloadListeners) {
			try {
				listener.afterReload();
			} catch (ClassNotFoundException |
					InstantiationException |
					IllegalAccessException |
					InvocationTargetException e) {
				throw e;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		get();
	}

	public static Supplier<ClassLoader> joinPublic(Collection<SimpleModuleLoader> modules) {
		return () -> new AggregateClassLoader(
				modules
						.stream()
						.map(SimpleModuleLoader::getClassLoadingPair)
						.map(ClassLoadingPair::getPublicClassLoader)
						.collect(Collectors.toList()));
	}
}
