package nl.positor.module.loading;

import nl.positor.module.definition.ModuleDefinition;

import java.util.function.Function;

/**
 * Created by Arien on 10-Jun-16.
 */
public interface ModuleClassPathMapping extends Function<ModuleDefinition, ClassPath> {
}
