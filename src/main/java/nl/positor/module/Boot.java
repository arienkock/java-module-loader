package nl.positor.module;

import nl.positor.module.loading.ModuleLoaderBuilder;

public class Boot {

	public static ModuleLoaderBuilder module(String string) {
		ModuleLoaderBuilder builder = new ModuleLoaderBuilder();
		return builder.module(string);
	}
	
}
