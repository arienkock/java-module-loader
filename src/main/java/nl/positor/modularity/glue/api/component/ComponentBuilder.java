package nl.positor.modularity.glue.api.component;

import java.net.URL;

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

    ComponentBuilder loadedFrom(URL... classPathUrls);

    ComponentBuilder withPublicApi(String... publicClassNames);
}
