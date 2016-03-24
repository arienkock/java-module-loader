package nl.positor.module;

public class Boot {

	public static ModuleLoaderBuilder module(String string) {
		ModuleLoaderBuilder builder = new ModuleLoaderBuilder();
		return builder.module(string);
	}
	
}
