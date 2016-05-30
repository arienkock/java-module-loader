package nl.positor.module.loading;

import nl.positor.module.Boot;
import nl.positor.module.Workspace;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.lang.ref.WeakReference;

import static org.junit.Assert.*;

public class SimpleModuleLoaderTest {
	@After
	public void after() throws IOException {
		Workspace.clean();
	}
	
	
	@Test
	public void testPublicAndPrivateInteraction() throws Exception {
		Workspace.compileModule("printer.Printer");
		SimpleModuleLoader loader = printerLoader();
		Object module = loader.get();
		
		assertNotNull(module);
		// module impl class was loaded using private classloader
		assertEquals(module.getClass(), loader.getClassLoadingPair().getPrivateClassLoader().loadClass("printer.PrinterImpl"));
		// public CL cannot find private class
		try {
			loader.getClassLoadingPair().getPublicClassLoader().loadClass("printer.PrinterImpl");
			fail();
		} catch (ClassNotFoundException e) {
		}
		// the module implements the interface loaded loaderFrom the public classloader
		assertTrue(loader.getClassLoadingPair().getPublicClassLoader().loadClass("printer.Printer").isAssignableFrom(module.getClass()));
		
		SimpleModuleLoader loader2 = printerLoader();
		assertFalse(loader.get() == loader2.get());
	}

	private SimpleModuleLoader printerLoader() throws IOException {
		return Boot
				.module("printer.PrinterImpl")
				.from(
						Workspace.modulePath("printer.Printer").resolve("api"), 
						Workspace.modulePath("printer.Printer").resolve("impl"))
				.build();
	}
	
	@Test
	public void testReloading() throws Exception {
		Workspace.compileModule("printer.Printer", "broken");
		SimpleModuleLoader loader = printerLoader();
		Object module = loader.get();
		
		assertEquals("BROKEN", module.toString());
		
		Workspace.compileModule("printer.Printer");
		WeakReference<Object> previousInstanceRef = new WeakReference<>(module.getClass());
		module = loader.reload();
		
		System.gc();
		assertEquals("", module.toString());
		
		// Wait for garbage collection to reclaim broken module instance
		long startOfLoop = System.currentTimeMillis();
		while (previousInstanceRef.get() != null && System.currentTimeMillis() - startOfLoop < 10000) {
			Thread.yield();
		}
		assertNull(previousInstanceRef.get());
	}

}
