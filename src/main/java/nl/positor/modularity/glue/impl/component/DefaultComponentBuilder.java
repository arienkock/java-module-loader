package nl.positor.modularity.glue.impl.component;

import nl.positor.modularity.glue.api.component.ComponentBuilder;
import nl.positor.modularity.glue.api.component.Dependency;
import nl.positor.modularity.glue.impl.util.Preconditions;

import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Arien on 28-Dec-16.
 */
public class DefaultComponentBuilder implements ComponentBuilder {
    private BiConsumer<String, DefaultComponentBuilder> nameCallback;
    private String name;
    private String className;
    private List<Dependency> constructorArguments;
    private List<Invocation> methodInvocations = new LinkedList<>();
    private String startMethodName;
    private String stopMethodName;
    private URL[] classPathUrls;
    private String[] publicClassNames;

    public DefaultComponentBuilder(BiConsumer<String, DefaultComponentBuilder> nameCallback) {
        this.nameCallback = nameCallback;
    }

    @Override
    public ComponentBuilder named(String name) {
        Preconditions.checkArgument(this.name == null, "Field 'name' can only be set once. Was %s and now attempted to set to %s", this.name, name);
        nameCallback.accept(name, this);
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

    @Override
    public ComponentBuilder loadedFrom(URL... classPathUrls) {
        Preconditions.checkArgument(this.classPathUrls == null, "Field 'classPathUrls' can only be set once. Was %s and now attempted to set to %s", this.classPathUrls, classPathUrls);
        this.classPathUrls = classPathUrls;
        return this;
    }

    @Override
    public ComponentBuilder withPublicApi(String... publicClassNames) {
        Preconditions.checkArgument(this.publicClassNames == null, "Field 'publicClassNames' can only be set once. Was %s and now attempted to set to %s", this.publicClassNames, publicClassNames);
        this.publicClassNames = publicClassNames;
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

    public List<Invocation> getMethodInvocations() {
        return methodInvocations;
    }

    public static class Invocation {
        private String methodName;
        private List<Dependency> arguments;

        public Invocation(String methodName, List<Dependency> arguments) {
            this.methodName = methodName;
            this.arguments = arguments;
        }

        public String getMethodName() {
            return methodName;
        }

        public List<Dependency> getArguments() {
            return arguments;
        }
    }
}
