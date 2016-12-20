package nl.positor.modularity.loading.impl;

import nl.positor.modularity.loading.api.InstanceHolder;
import nl.positor.modularity.loading.api.InstanceProvider;
import nl.positor.modularity.loading.api.Instantiator;
import nl.positor.modularity.loading.api.InstantiatorBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arien on 16-Dec-16.
 */
public class DefaultInstantiatorBuilder implements InstantiatorBuilder {
    private String className;
    private InstanceProvider[] constructorArguments = new InstanceProvider[] {};
    private Map<String, InstanceProvider[]> methodParametersMap = new HashMap<>();

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
    public InstantiatorBuilder constructWith(InstanceProvider... constructorArguments) {
        this.constructorArguments = constructorArguments;
        return this;
    }

    @Override
    public InstantiatorBuilder callMethodWith(String methodName, InstanceProvider... constructorArguments) {
        methodParametersMap.put(methodName, constructorArguments);
        return this;
    }

    @Override
    public Instantiator build() {
        return new DefaultInstantiator(className, constructorArguments, methodParametersMap);
    }
}
