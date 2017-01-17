package nl.positor.modularity.glue.impl;

import nl.positor.modularity.glue.api.ComponentBuilder;
import nl.positor.modularity.glue.api.Dependency;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Arien on 28-Dec-16.
 */
public class DefaultComponentBuilder implements ComponentBuilder {
    private DefaultDependencyGraphBuilder dependencyGraphBuilder;
    private String name;
    private String className;
    private List<Dependency> constructorArguments;
    private List<Invocation> methodInvocations = new LinkedList<>();
    private String startMethodName;
    private String stopMethodName;

    public DefaultComponentBuilder(DefaultDependencyGraphBuilder dependencyGraphBuilder) {
        this.dependencyGraphBuilder = dependencyGraphBuilder;
    }

    @Override
    public ComponentBuilder named(String name) {
        Preconditions.checkArgument(this.name == null, "Field 'name' can only be set once. Was %s and now attempted to set to %s", this.name, name);
        dependencyGraphBuilder.registerNamedComponent(name, this);
        this.name = name;
        return this;
    }

    @Override
    public ComponentBuilder withImplementingClass(String className) {
        Preconditions.checkArgument(this.className == null, "Field 'className' can only be set once. Was %s and now attempted to set to %s", this.className, className);
        this.className = className;
        return this;
    }

    @Override
    public ComponentBuilder createdByCallingConstructorWith(Dependency... constructorArguments) {
        Preconditions.checkArgument(this.constructorArguments == null, "Field 'constructorArguments' can only be set once.");
        this.constructorArguments = Arrays.asList(constructorArguments);
        return this;
    }

    @Override
    public ComponentBuilder thenCalling(String methodName, Dependency... methodArguments) {
        methodInvocations.add(new Invocation(methodName, Arrays.asList(methodArguments)));
        return this;
    }

    @Override
    public ComponentBuilder startedByCalling(String methodName) {
        Preconditions.checkArgument(this.startMethodName == null, "Field 'startMethodName' can only be set once. Was %s and now attempted to set to %s", this.startMethodName, methodName);
        this.startMethodName = methodName;
        return this;
    }

    @Override
    public ComponentBuilder shutdownByCalling(String methodName) {
        Preconditions.checkArgument(this.stopMethodName == null, "Field 'stopMethodName' can only be set once. Was %s and now attempted to set to %s", this.stopMethodName, methodName);
        this.stopMethodName = methodName;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }

    public List<Dependency> getConstructorArguments() {
        return constructorArguments;
    }

    public String getStartMethodName() {
        return startMethodName;
    }

    public String getStopMethodName() {
        return stopMethodName;
    }

    public List<Dependency> getAllDependencies() {
        return Stream.concat(
                this.constructorArguments.stream(),
                methodInvocations.stream().flatMap(i -> i.arguments.stream()))
            .collect(Collectors.toList());
    }

    static class Invocation {
        private String methodName;
        private List<Dependency> arguments;

        public Invocation(String methodName, List<Dependency> arguments) {
            this.methodName = methodName;
            this.arguments = arguments;
        }
    }
}
