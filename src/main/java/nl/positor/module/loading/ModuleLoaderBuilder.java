package nl.positor.module.loading;

import nl.positor.module.wiring.ModuleContainer;
import nl.positor.util.Always;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.function.Supplier;

public class ModuleLoaderBuilder {
	private String className;
	private URL[] privateClassPath;
	private URL[] publicClassPath;

	public ModuleLoaderBuilder module(String className) {
		this.className = className;
		return this;
	}

	/**
	 * Sets a public and private classpaths consisting of one path each.
	 *
	 * @param publicPath public classpath
	 * @param privatePath private classpath
	 * @return this
	 * @throws MalformedURLException if paths cannot be converted to {@link URL} objects
	 */
	public ModuleLoaderBuilder from(Path publicPath, Path privatePath)
			throws MalformedURLException {
		publicClassPath = new URL[] { publicPath.toUri().toURL() };
		privateClassPath = new URL[] { privatePath.toUri().toURL() };
		return this;
	}

	/**
	 * Creates a new {@link ModuleLoader} and optionally a
	 * {@link ModuleContainer} if any intra-module wiring is required.
	 *
	 * @return the result of the build
	 */
	public ModuleLoader build() {
		URL[] privateClassPathL = privateClassPath;
		URL[] publicClassPathL = publicClassPath;
		Supplier<ClassLoadingPair> classLoadingSource =
				() -> new ClassLoadingPair(
						() -> Thread.currentThread().getContextClassLoader(),
						pubCl -> new URLClassLoader(privateClassPathL, pubCl),
						parentCl -> new URLClassLoader(publicClassPathL,parentCl));
		return new ModuleLoader(className, classLoadingSource);
	}
}
