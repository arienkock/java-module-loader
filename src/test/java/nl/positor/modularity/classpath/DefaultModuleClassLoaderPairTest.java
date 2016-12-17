package nl.positor.modularity.classpath;

import nl.positor.modularity.classpath.api.ClassNameCheck;
import nl.positor.modularity.classpath.api.ModuleDefinition;
import nl.positor.modularity.classpath.impl.AggregateClassLoader;
import nl.positor.modularity.classpath.impl.DefaultModuleClassLoaderPair;
import nl.positor.modularity.classpath.impl.DefaultModuleDefinition;
import nl.positor.modularity.classpath.impl.ListClassNameCheck;
import nl.positor.modularity.classpath.testcase_modules.caller.ToStringCaller;
import nl.positor.modularity.classpath.testcase_modules.caller.ToStringCallerExample;
import nl.positor.modularity.classpath.testcase_modules.global_logger.GlobalLogger;
import nl.positor.modularity.classpath.testcase_modules.tostringer_api.ToStringer;
import nl.positor.modularity.classpath.testcase_modules.tostringer_impl1.ToStringerExample;
import nl.positor.modularity.classpath.testcase_modules.tostringer_impl2.ToStringerExample2;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by Arien on 16-Dec-16.
 */
public class DefaultModuleClassLoaderPairTest {
    @Test
    public void testLoading() throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        URL[] thisProjectClassPath = ((URLClassLoader) DefaultModuleClassLoaderPairTest.class.getClassLoader()).getURLs();
        ClassLoader systemClassLoader = DefaultModuleClassLoaderPairTest.class.getClassLoader().getParent();
        ModuleDefinition globalLogger = globalLogger(thisProjectClassPath);
        ModuleDefinition toStringerApi = toStringerApi(thisProjectClassPath);
        ModuleDefinition toStringerImpl1 = toStringerImpl1(thisProjectClassPath);
        ModuleDefinition toStringerImpl2 = toStringerImpl2(thisProjectClassPath);
        ModuleDefinition toStringCaller = toStringCaller(thisProjectClassPath);

        DefaultModuleClassLoaderPair globalLoggerClp = new DefaultModuleClassLoaderPair(globalLogger, systemClassLoader);
        DefaultModuleClassLoaderPair toStringerApiClp = new DefaultModuleClassLoaderPair(toStringerApi, systemClassLoader);
        DefaultModuleClassLoaderPair toStringerImpl1Clp = new DefaultModuleClassLoaderPair(toStringerImpl1, new AggregateClassLoader(systemClassLoader, toStringerApiClp.getOuterClassLoader()));
        DefaultModuleClassLoaderPair toStringerImpl2Clp = new DefaultModuleClassLoaderPair(toStringerImpl2, new AggregateClassLoader(systemClassLoader, toStringerApiClp.getOuterClassLoader()));
        DefaultModuleClassLoaderPair toStringCallerClp = new DefaultModuleClassLoaderPair(toStringCaller, new AggregateClassLoader(systemClassLoader, toStringerApiClp.getOuterClassLoader(), globalLoggerClp.getOuterClassLoader()));
        // Load stringer caller
        Object toStringerCallerExample = toStringCallerClp
                .getInnerClassLoader()
                .loadClass(ToStringCallerExample.class.getName())
                .newInstance();
        assertTrue(
                "ToStringerCallerExample should be instantiated by the inner classloader of its module",
                toStringerCallerExample.getClass().getClassLoader() == toStringCallerClp.getInnerClassLoader());
        assertFalse(
                "Ded we mess up the test and use our own CL to load?",
                toStringerCallerExample.getClass().equals(ToStringCallerExample.class));
        // Load first implementation of ToStringer API
        Object toStringerImpl1Object = toStringerImpl1Clp
                .getInnerClassLoader()
                .loadClass(ToStringerExample.class.getName())
                .newInstance();
        Class<?> toStringerInterfaceLoadedViaImpl1OuterClassLoader = toStringerImpl1Clp.getOuterClassLoader().loadClass(ToStringer.class.getName());
        assertTrue(
                "ToStringer API interface ended up being loaded by the same CL",
                toStringerInterfaceLoadedViaImpl1OuterClassLoader.isAssignableFrom(toStringerImpl1Object.getClass()));
        // Interface method invoked on implementation
        Method callToString = toStringCallerClp.getOuterClassLoader().loadClass(ToStringCaller.class.getName()).getMethod("callToString", toStringerInterfaceLoadedViaImpl1OuterClassLoader);
        callToString.invoke(toStringerCallerExample, toStringerImpl1Object);
        assertEquals("[To stringer called and returned This was an example]", toStringerCallerExample.toString());
        // Load second impl, do same call
        Object toStringerImpl2Object = toStringerImpl2Clp
                .getInnerClassLoader()
                .loadClass(ToStringerExample2.class.getName())
                .newInstance();
        callToString.invoke(toStringerCallerExample, toStringerImpl2Object);
        assertEquals("[To stringer called and returned This was an example, To stringer called and returned This was another example.]", toStringerCallerExample.toString());
        // try to find unrelated class from outer CL
        try {
            toStringerApiClp.getOuterClassLoader().loadClass(GlobalLogger.class.getName());
            fail("ToStringer api has no external dependency on global logger. outer CL shouldn't be able to find it");
        } catch (ClassNotFoundException e) {
        }
    }

    private ClassNameCheck classCheck(String... names) {
        return new ListClassNameCheck(Arrays.asList(names));
    }

    private ModuleDefinition toStringCaller(URL[] thisProjectClassPath) {
        return new DefaultModuleDefinition(
                classCheck(ToStringCaller.class.getName()),
                classCheck(GlobalLogger.class.getName(), ToStringer.class.getName()),
                thisProjectClassPath
        );
    }

    private ModuleDefinition globalLogger(URL[] thisProjectClassPath) {
        return new DefaultModuleDefinition(
                classCheck(GlobalLogger.class.getName()),
                n -> false,
                thisProjectClassPath
        );
    }

    private ModuleDefinition toStringerImpl2(URL[] thisProjectClassPath) {
        return new DefaultModuleDefinition(
                n -> false,
                classCheck(ToStringer.class.getName()),
                thisProjectClassPath
        );
    }

    private ModuleDefinition toStringerImpl1(URL[] thisProjectClassPath) {
        return new DefaultModuleDefinition(
                n -> false,
                classCheck(ToStringer.class.getName()),
                thisProjectClassPath
        );
    }

    private ModuleDefinition toStringerApi(URL[] thisProjectClassPath) {
        return new DefaultModuleDefinition(
                classCheck(ToStringer.class.getName()),
                n -> false,
                thisProjectClassPath
        );
    }

}