package nl.positor.module.loading;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

/**
 * Lazily instantiates an object of the type specified by {@link #className} and
 * keeps a reference to it when created through {@link #peek()}. The reference
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
public class ModuleLoader {
	private final String className;
    private final Supplier<ClassLoadingPair> classLoadingSource;
    private Object moduleInstance;
    private ClassLoadingPair classLoadingPair;

	/**
	 * Creates a new instance. Use {@link ModuleLoaderBuilder} for convenience.
	 *
	 * @param className Qualified name of the module's implementing class
	 * @param privateClassLoaderSupplier supplier of the classloader used for private classes. null meaning there are only public classes.
	 * @param publicClassLoaderSupplier supplier of the classloader used for public classes. must not be null.
	 */
	public ModuleLoader(String className, Supplier<ClassLoadingPair> classLoadingSource) {
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
	 * {@link #peek()} if you want to create a singleton (within the scope of
	 * this loader) module.
     *
     * Instantiates the module class using its zero-argument constructor, through
     * reflection as recommended by <a href="https://docs.oracle.com/javase/tutorial/reflect/member/ctorInstance.html">
     * this Oracle tutorial</a>.
	 *
	 * @return a new module
	 * @throws ClassNotFoundException propagated from inner classloaders
	 * @throws InstantiationException propagated from inner classloaders
	 * @throws IllegalAccessException propagated from inner classloaders
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
            if (ctor.getGenericParameterTypes().length == 0)
                break;
        }
		moduleInstance = ctor.newInstance();
		return moduleInstance;
	}

	/**
	 * Lazily initializes a new singleton module. Subsequent calls return the
	 * same instance until one of the {@link #reload()} methods is called.
	 *
	 * @return the current module instance
	 * @throws ClassNotFoundException propagated from inner classloaders
	 * @throws InstantiationException propagated from inner classloaders
	 * @throws IllegalAccessException propagated from inner classloaders
     * @throws InvocationTargetException propagated from {@ling #load}
	 */
	public Object peek() throws
            ClassNotFoundException,
            InstantiationException,
            IllegalAccessException,
            InvocationTargetException {
		if (moduleInstance == null) {
			moduleInstance = load();
		}
		return moduleInstance;
	}

    /**
     * Unreferences the module instance and both classloaders,
     * then calls {@link #peek()} resulting in a new module instance.
     *
     * @return The new singleton instance
	 * @throws ClassNotFoundException propagated from inner classloaders
	 * @throws InstantiationException propagated from inner classloaders
	 * @throws IllegalAccessException propagated from inner classloaders
     * @throws InvocationTargetException propagated from {@ling #load}
	 */
	public Object reload() throws
            ClassNotFoundException,
            InstantiationException,
            IllegalAccessException,
            InvocationTargetException {
        classLoadingPair = null;
		moduleInstance = null;
		return peek();
	}

}
