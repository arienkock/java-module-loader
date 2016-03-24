package nl.positor.module;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ModuleLoaderBuilder {
	private String className;
	private URL[] privateClassPath;
	private URL[] publicClassPath;
	private Map<ModuleLoader, Set<ModuleLoader>> dependencies = new HashMap<>();
	
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
	
	public ModuleLoaderBuilder referencing(ModuleLoader... dependencies) {
		throw new Error();
	}

	/**
	 * Creates a new {@link ModuleLoader} and optionally a
	 * {@link ModuleContainer} if any intra-module wiring is required.
	 * 
	 * @return the result of the build
	 */
	public Result build() {
		return new Result(
				null,
				new ModuleLoader(className, privateClassPath, publicClassPath));
	}
	
	public static class Result {
		public final ModuleContainer container;
		public final ModuleLoader loader;
		
		public Result(ModuleContainer container, ModuleLoader loader) {
			this.container = container;
			this.loader = loader;
		}
	}
}
