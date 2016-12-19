package nl.positor.modularity.glue.impl;

import nl.positor.modularity.classpath.api.ModuleClassLoaderPair;
import nl.positor.modularity.classpath.api.ModuleDefinition;
import nl.positor.modularity.classpath.impl.DefaultModuleClassLoaderPair;
import nl.positor.modularity.glue.api.Blueprint;
import nl.positor.modularity.glue.api.Component;
import nl.positor.modularity.loading.api.InstanceHolder;

import java.net.URL;
import java.util.function.Supplier;

/**
 * Created by Arien on 17-Dec-16.
 */
public class DefaultComponent implements Component, InstanceHolder {
    private final Blueprint blueprint;
    private final Supplier<URL[]> classPath;
    private final Supplier<String[]> exposedClasses;
    private DefaultModuleClassLoaderPair moduleClassLoaderPair;
    private Object instance;

    public DefaultComponent(Blueprint blueprint, Supplier<URL[]> classPath, Supplier<String[]> exposedClasses) {
        this.blueprint = blueprint;
        this.classPath = classPath;
        this.exposedClasses = exposedClasses;
    }

    @Override
    public synchronized void start() {

    }

    @Override
    public synchronized void clean() {

    }

    @Override
    public synchronized void stop() {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void reload() {

    }

    @Override
    public synchronized Object get() {
        if (instance == null) {
            instance = blueprint.create(getInnerClassLoader());
        }
        return instance;
    }

    private ClassLoader getInnerClassLoader() {
        URL[] urls = classPath.get();
        if (urls)
        return null;
    }

    @Override
    public synchronized void release() {

    }

    public synchronized ModuleClassLoaderPair getClassLoaderPair() {
        if (moduleClassLoaderPair == null) {
            ClassLoader moduleParentCl;
            ModuleDefinition md;
            moduleClassLoaderPair = new DefaultModuleClassLoaderPair(md, moduleParentCl);
        }
        return moduleClassLoaderPair;
    }
}
