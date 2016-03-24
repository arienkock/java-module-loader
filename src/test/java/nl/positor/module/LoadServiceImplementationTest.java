package nl.positor.module;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;

import org.junit.After;
import org.junit.Test;

public class LoadServiceImplementationTest {
	@After
	public void after() throws IOException {
		Workspace.clean();
	}
	
	
	@Test
	public void testPublicAndPrivateInteraction() throws Exception {
		Workspace.compileModule("printer.Printer");
		ModuleLoader loader = printerLoader();
		Object module = loader.peek();
		
		assertNotNull(module);
		// module impl class was loaded using private classloader
		assertEquals(module.getClass(), loader.getPrivateClassLoader().loadClass("printer.PrinterImpl"));
		// public CL cannot find private class
		try {
			loader.getPublicClassLoader().loadClass("printer.PrinterImpl");
			fail();
		} catch (ClassNotFoundException e) {
		}
		// the module implements the interface loaded from the public classloader
		assertTrue(loader.getPublicClassLoader().loadClass("printer.Printer").isAssignableFrom(module.getClass()));
		
		ModuleLoader loader2 = printerLoader();
		assertFalse(loader.peek() == loader2.peek());
	}

	private ModuleLoader printerLoader() throws MalformedURLException, IOException {
		return Boot
				.module("printer.PrinterImpl")
				.from(
						Workspace.modulePath("printer.Printer").resolve("api"), 
						Workspace.modulePath("printer.Printer").resolve("impl"))
				.build()
				.loader;
	}
	
	@Test
	public void testReloading() throws Exception {
		Workspace.compileModule("printer.Printer", "broken");
		ModuleLoader loader = printerLoader();
		Object module = loader.peek();
		
		assertEquals(module.toString(), "BROKEN");
		
		Workspace.compileModule("printer.Printer");
		WeakReference<Object> previousInstanceRef = new WeakReference<>(module.getClass());
		module = loader.reload();
		
		System.gc();
		assertEquals(module.toString(), "");
		
		// Wait for garbage collection to reclaim broken module instance
		long startOfLoop = System.currentTimeMillis();
		while (previousInstanceRef.get() != null && System.currentTimeMillis() - startOfLoop < 10000) {
			Thread.yield();
		}
		assertNull(previousInstanceRef.get());
	}
	
	@Test
	public void testReloadingImplementation() throws Exception {
		Workspace.compileModule("printer.Printer", "broken");
		ModuleLoader loader = printerLoader();
		Object module = loader.peek();
		
		assertEquals(module.toString(), "BROKEN");
		
		Workspace.compileModule("printer.Printer");
		WeakReference<Class<?>> previousInstanceRef = new WeakReference<>(module.getClass());
		WeakReference<Class<?>> publicInterfaceRef = 
				new WeakReference<>(loader.getPublicClassLoader().loadClass("printer.Printer"));
		module = loader.reload(false);
		
		System.gc();
		assertEquals(module.toString(), "");
		
		// Wait for garbage collection to reclaim broken module instance
		long startOfLoop = System.currentTimeMillis();
		while (previousInstanceRef.get() != null && System.currentTimeMillis() - startOfLoop < 10000) {
			Thread.yield();
		}
		assertNull(previousInstanceRef.get());
		assertNotNull(publicInterfaceRef.get());
		assertTrue(publicInterfaceRef.get().isAssignableFrom(loader.peek().getClass()));
	}
}
