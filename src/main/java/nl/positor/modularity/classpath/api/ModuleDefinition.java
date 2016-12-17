package nl.positor.modularity.classpath.api;

import java.net.URL;

/**
 * Created by Arien on 26-May-16.
 */
public interface ModuleDefinition {
    ClassNameCheck getPublicClassCheck();

    ClassNameCheck getExternalClassCheck();

    URL[] getClassPath();
}
