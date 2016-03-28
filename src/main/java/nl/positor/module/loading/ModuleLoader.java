package nl.positor.module.loading;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * Lazily instantiates an object of the type specified by {@link #className} and
 * keeps a reference to it when created through {@link #peek()}. The reference
 * making it a singleton within the scope of a loader instance until the
 * reference is unset using the {@link #reload()} methods.
 * 
 * @author Arien
 *
 */
public class ModuleLoader {
	private final String className;
	private ClassLoader publicClassLoader;
	private ClassLoader privateClassLoader;
	private Object moduleInstance;
	private URL[] publicClassPath;
	private URL[] privateClassPath;

	/**
	 * Creates a new instance. Use {@link ModuleLoaderBuilder} for convenience.
	 * 
	 * @param className Qualified name of the module's implementing class 
	 * @param privateClassPath private classpath sources
	 * @param publicClassPath public classpath sources
	 */
	public ModuleLoader(String className, URL[] privateClassPath, URL[] publicClassPath) {
		this.className = className;
		this.privateClassPath = privateClassPath;
		this.publicClassPath = publicClassPath;
	}

	/**
	 * Loads a new instance of the module with the currently active
	 * {@link ClassLoader}s without associating it with this loader. Use
	 * {@link #peek()} if you want to create a singleton (within the scope of
	 * this loader) module.
	 * 
	 * @return a new module
	 * @throws ClassNotFoundException propagated from inner classloaders
	 * @throws InstantiationException propagated from inner classloaders
	 * @throws IllegalAccessException propagated from inner classloaders
	 */
	public Object load() throws 
			ClassNotFoundException, 
			InstantiationException, 
			IllegalAccessException {
		ClassLoader cl = getPrivateClassLoader();
		Class<?> moduleClass = cl.loadClass(className);
		moduleInstance = moduleClass.newInstance();
		return moduleInstance;
	}

	ClassLoader getPublicClassLoader() {
		if (publicClassLoader == null) {
			publicClassLoader = new URLClassLoader(getPublicClassPath());
		}
		return publicClassLoader;
	}

	URL[] getPublicClassPath() {
		return publicClassPath;
	}

	ClassLoader getPrivateClassLoader() {
		if (privateClassLoader == null) {
			privateClassLoader = 
					new URLClassLoader(
							getPrivateClassPath(), 
							getPublicClassLoader());
		}
		return privateClassLoader;	
	}

	URL[] getPrivateClassPath() {
		return privateClassPath;
	}

	/**
	 * Lazily initializes a new singleton module. Subsequent calls return the
	 * same instance until one of the {@link #reload()} methods is called.
	 * 
	 * @return the current module instance
	 * @throws ClassNotFoundException propagated from inner classloaders
	 * @throws InstantiationException propagated from inner classloaders
	 * @throws IllegalAccessException propagated from inner classloaders
	 */
	public Object peek() throws 
			ClassNotFoundException, 
			InstantiationException, 
			IllegalAccessException {
		if (moduleInstance == null) {
			moduleInstance = load();
		}
		return moduleInstance;
	}

	/**
	 * Invokes reload(true). See {@link #reload(boolean)}
	 * 
	 * @return the new current module instance
	 * @throws ClassNotFoundException propagated from inner classloaders
	 * @throws InstantiationException propagated from inner classloaders
	 * @throws IllegalAccessException propagated from inner classloaders
	 */
	public Object reload() throws 
			ClassNotFoundException, 
			InstantiationException, 
			IllegalAccessException {
		return reload(true);
	}

	/**
	 * Unreferences the module instance and private classloader (optionally the
	 * public {@link ClassLoader}) and calls {@link #peek()} resulting in a new
	 * module instance.
	 * 
	 * @param reloadPublicClasses
	 *            whether or not the public class loader should be
	 *            re-instantiated
	 * @return The new singleton instance
	 * @throws ClassNotFoundException propagated from inner classloaders
	 * @throws InstantiationException propagated from inner classloaders
	 * @throws IllegalAccessException propagated from inner classloaders
	 */
	public Object reload(boolean reloadPublicClasses) throws 
			ClassNotFoundException, 
			InstantiationException, 
			IllegalAccessException {
		if (reloadPublicClasses) {
			publicClassLoader = null;
		}
		privateClassLoader = null;
		moduleInstance = null;
		return peek();
	}

}
