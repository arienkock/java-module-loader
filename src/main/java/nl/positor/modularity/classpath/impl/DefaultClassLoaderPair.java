package nl.positor.modularity.classpath.impl;

import nl.positor.modularity.classpath.api.ModuleDefinition;
import nl.positor.modularity.classpath.api.ModuleClassLoaderPair;

import java.util.function.Predicate;

/**
 * Created by Arien on 16-Dec-16.
 */
public class DefaultClassLoaderPair implements ModuleClassLoaderPair {
    private final ClassLoader innerClassLoader;
    private final ClassLoader outerClassLoader;

    public DefaultClassLoaderPair(ModuleDefinition moduleDefinition, ClassLoader parentClassLoader) {
        final Predicate<String> isPublic = moduleDefinition.getPublicClassCheck();
        final Predicate<String> isNotPublic = isPublic.negate();
        final Predicate<String> isNotExternal = moduleDefinition.getExternalClassCheck().negate();
        this.outerClassLoader = new FilteredClassLoader(moduleDefinition.getClassPath(), parentClassLoader, isPublic.and(isNotExternal));
        this.innerClassLoader = new FilteredClassLoader(moduleDefinition.getClassPath(), this.outerClassLoader, isNotExternal.and(isNotPublic));
    }

    @Override
    public ClassLoader getOuterClassLoader() {
        return outerClassLoader;
    }

    @Override
    public ClassLoader getInnerClassLoader() {
        return innerClassLoader;
    }
}
