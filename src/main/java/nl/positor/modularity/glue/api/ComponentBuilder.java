package nl.positor.modularity.glue.api;

import nl.positor.modularity.classpath.api.ModuleDefinition;

/**
 * Created by Arien on 17-Dec-16.
 */
public interface ComponentBuilder {
    ComponentBuilder named(String name);

    ComponentBuilder implementingClass(String className);

    ComponentBuilder callingConstructor(Dependency... constructorArguments);

    ComponentBuilder thenCalling(String methodName, Dependency... constructorArguments);

    ComponentBuilder shutdownByCalling(String methodName);

    ComponentBuilder cleanUpByCalling(String methodName);

    ComponentBuilder loadedFrom(ModuleDefinition moduleDefinition);
}
