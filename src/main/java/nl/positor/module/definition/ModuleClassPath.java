package nl.positor.module.definition;

import java.net.URL;

/**
 * Created by Arien on 27-May-16.
 */
public interface ModuleClassPath {
    ModuleDefinition getDefinition();
    Iterable<URL> getClassPath();
}
