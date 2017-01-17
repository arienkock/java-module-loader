package nl.positor.modularity.glue.impl;

import nl.positor.modularity.glue.api.DependencyGraph;
import nl.positor.modularity.glue.api.DependencyGraphBuilder;
import nl.positor.modularity.glue.api.component.Component;
import nl.positor.modularity.glue.api.component.ComponentBuilder;
import nl.positor.modularity.glue.api.component.Dependency;
import nl.positor.modularity.glue.impl.component.DefaultComponent;
import nl.positor.modularity.glue.impl.component.DefaultComponentBuilder;
import nl.positor.modularity.glue.impl.component.DefaultDependency;
import nl.positor.modularity.glue.impl.component.LifecycleComponent;
import nl.positor.modularity.glue.impl.util.Preconditions;
import nl.positor.modularity.lifecycle.impl.DefaultRestarter;
import nl.positor.modularity.loading.api.InstanceProvider;
import nl.positor.modularity.loading.api.Instantiator;
import nl.positor.modularity.loading.api.InstantiatorBuilder;
import nl.positor.modularity.loading.impl.DefaultInstantiatorBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DefaultDependencyGraphBuilder implements DependencyGraphBuilder {
    private Map<String, DefaultComponentBuilder> componentMap = new HashMap<>();

    @Override
    public ComponentBuilder withComponent() {
        return new DefaultComponentBuilder(this::registerNamedComponent);
    }

    @Override
    public Dependency dependencyNamed(String name) {
        Objects.requireNonNull(componentMap.get(name), "No component in this dependency graph by that name: " + name);
        return new DefaultDependency(name);
    }

    @Override
    public DependencyGraph build() {
        AtomicReference<DependencyGraph> graphHolder = new AtomicReference<>();
        Map<Component, List<Dependency>> depLookup = new HashMap<>();
        Map<String, LifecycleComponent> builtComponentMap = new HashMap<>();
        for (Map.Entry<String, DefaultComponentBuilder> entry : componentMap.entrySet()) {
            DefaultComponentBuilder componentBuilder = entry.getValue();
            DefaultComponent component = new DefaultComponent(toInstantiator(componentBuilder, graphHolder::get), entry.getKey(), componentBuilder.getStartMethodName(), componentBuilder.getStopMethodName());
            builtComponentMap.put(componentBuilder.getName(), component);
            depLookup.put(component, componentBuilder.getAllDependencies());
        }
        DefaultDependencyGraph dependencyGraph = new DefaultDependencyGraph(new DefaultRestarter(), builtComponentMap, depLookup);
        graphHolder.set(dependencyGraph);
        return dependencyGraph;
    }

    private void registerNamedComponent(String name, DefaultComponentBuilder componentBuilder) {
        DefaultComponentBuilder previousComponent = componentMap.put(name, componentBuilder);
        Preconditions.checkArgument(previousComponent == null, "Duplicate registration of a component with the name %s", name);
    }


    private Instantiator toInstantiator(DefaultComponentBuilder componentBuilder, Supplier<DependencyGraph> dependencyGraphSupplier) {
        InstanceProvider[] constructorArgumentProviders = new InstanceProvider[componentBuilder.getConstructorArguments().size()];
        List<InstanceProvider> constructorArgumentProvidersList = componentBuilder.getConstructorArguments().stream()
                .<InstanceProvider>map(dep -> () -> dependencyGraphSupplier.get().getComponentByName(dep.getTargetName()).getInstance())
                .collect(Collectors.toList());
        constructorArgumentProviders = constructorArgumentProvidersList.toArray(constructorArgumentProviders);
        InstantiatorBuilder instantiatorBuilder = new DefaultInstantiatorBuilder()
                .constructWith(constructorArgumentProviders)
                .forClass(componentBuilder.getClassName());
        for (DefaultComponentBuilder.Invocation invocation : componentBuilder.getMethodInvocations()) {
            InstanceProvider[] methodArgs = invocation.getArguments().toArray(new InstanceProvider[invocation.getArguments().size()]);
            instantiatorBuilder.callMethodWith(invocation.getMethodName(), methodArgs);
        }
        return instantiatorBuilder.build();
    }
}
