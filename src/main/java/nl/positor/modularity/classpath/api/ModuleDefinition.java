package nl.positor.modularity.classpath.api;

import java.net.URL;

/**
 * Created by Arien on 26-May-16.
 */
public interface ModuleDefinition {
    PublicClassCheck getPublicClassCheck();

    ExternalClassCheck getExternalClassCheck();

    URL[] getClassPath();
}
