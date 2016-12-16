package nl.positor.modularity.classpath.api;

/**
 * Created by Arien on 16-Dec-16.
 */
public interface ModuleClassLoaderPair {
    ClassLoader getOuterClassLoader();

    ClassLoader getInnerClassLoader();
}
