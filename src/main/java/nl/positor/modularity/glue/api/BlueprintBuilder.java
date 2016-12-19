package nl.positor.modularity.glue.api;

/**
 * Created by Arien on 17-Dec-16.
 */
public interface BlueprintBuilder {

    BlueprintBuilder forClass(String className);

    BlueprintBuilder nullaryConstructor();

    BlueprintBuilder callingConstructorWith(Dependency... constructorArguments);

    BlueprintBuilder thenCalling(String methodName, Dependency... constructorArguments);

    BlueprintBuilder shutdownByCalling(String methodName);

    BlueprintBuilder cleanUpByCalling(String methodName);

    Blueprint build();

    ComponentBuilder finish();
}
