package nl.positor.modularity.loading.api;

/**
 * Created by Arien on 16-Dec-16.
 */
public interface InstantiatorBuilder {

    InstantiatorBuilder forClass(String className);

    InstantiatorBuilder constructWith(InstanceHolder... constructorArguments);

    InstantiatorBuilder callMethodWith(String methodName, InstanceHolder... constructorArguments);

    Instantiator build();
}
