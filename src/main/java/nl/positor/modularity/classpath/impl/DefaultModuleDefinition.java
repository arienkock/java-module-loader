package nl.positor.modularity.classpath.impl;

import nl.positor.modularity.classpath.api.ClassNameCheck;
import nl.positor.modularity.classpath.api.ModuleDefinition;

import java.net.URL;

/**
 * Created by Arien on 16-Dec-16.
 */
public class DefaultModuleDefinition implements ModuleDefinition {
    private final ClassNameCheck publicClassCheck;
    private final ClassNameCheck externalClassChech;
    private final URL[] classPath;

    public DefaultModuleDefinition(ClassNameCheck publicClassCheck, ClassNameCheck externalClassChech, URL[] classPath) {
        this.publicClassCheck = publicClassCheck;
        this.externalClassChech = externalClassChech;
        this.classPath = classPath;
    }

    @Override
    public ClassNameCheck getPublicClassCheck() {
        return publicClassCheck;
    }

    @Override
    public ClassNameCheck getExternalClassCheck() {
        return externalClassChech;
    }

    @Override
    public URL[] getClassPath() {
        return classPath;
    }
}
