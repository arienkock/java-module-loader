package nl.positor.module.loading;

import nl.positor.module.definition.ModuleDefinition;

import java.util.function.Function;

/**
 * Created by Arien on 26-May-16.
 */
public interface ModuleLoaderOnly {
    Object load();
    ModuleDefinition getDefinition();
}
