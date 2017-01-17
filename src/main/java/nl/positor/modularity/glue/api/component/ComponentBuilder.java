package nl.positor.modularity.glue.api.component;

/**
 * Created by Arien on 17-Dec-16.
 */
public interface ComponentBuilder {
    ComponentBuilder named(String name);

    ComponentBuilder withImplementingClass(String className);

    default ComponentBuilder createdByCallingNullaryConstructor() {
        return createdByCallingConstructorWith();
    }

    ComponentBuilder createdByCallingConstructorWith(Dependency... constructorArguments);

    ComponentBuilder thenCalling(String methodName, Dependency... methodArguments);

    ComponentBuilder startedByCalling(String methodName);

    ComponentBuilder shutdownByCalling(String methodName);
}
