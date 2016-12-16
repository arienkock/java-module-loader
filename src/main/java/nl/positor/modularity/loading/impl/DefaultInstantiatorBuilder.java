package nl.positor.modularity.loading.impl;

import nl.positor.modularity.loading.api.InstanceHolder;
import nl.positor.modularity.loading.api.Instantiator;
import nl.positor.modularity.loading.api.InstantiatorBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arien on 16-Dec-16.
 */
public class DefaultInstantiatorBuilder implements InstantiatorBuilder {
    private String className;
    private InstanceHolder[] constructorArguments = new InstanceHolder[] {};
    private Map<String, InstanceHolder[]> methodParametersMap = new HashMap<>();

    public static InstantiatorBuilder newForClass(String className) {
        DefaultInstantiatorBuilder builder = new DefaultInstantiatorBuilder();
        return builder.forClass(className);
    }

    @Override
    public InstantiatorBuilder forClass(String className) {
        this.className = className;
        return this;
    }

    @Override
    public InstantiatorBuilder constructWith(InstanceHolder... constructorArguments) {
        this.constructorArguments = constructorArguments;
        return this;
    }

    @Override
    public InstantiatorBuilder callMethodWith(String methodName, InstanceHolder... constructorArguments) {
        methodParametersMap.put(methodName, constructorArguments);
        return this;
    }

    @Override
    public Instantiator build() {
        return new DefaultInstantiator(className, constructorArguments, methodParametersMap);
    }
}
