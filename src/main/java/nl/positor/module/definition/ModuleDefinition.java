package nl.positor.module.definition;

import nl.positor.module.loading.ModuleLoader;

import java.net.URL;
import java.util.Optional;
import java.util.Set;

/**
 * Created by Arien on 26-May-16.
 */
public interface ModuleDefinition {
    Optional<String> getEntryPoint();
    Set<String> getPublicClasses();
    Iterable<ModuleDefinition> getDependencies();
}
