package nl.positor.modularity.glue.impl;

import nl.positor.modularity.glue.api.DependencyGraph;
import nl.positor.modularity.glue.api.component.Component;
import nl.positor.modularity.glue.api.component.Dependency;
import nl.positor.modularity.glue.impl.component.DefaultDependency;
import nl.positor.modularity.glue.impl.component.LifecycleComponent;
import nl.positor.modularity.lifecycle.api.DependencyLookup;
import nl.positor.modularity.lifecycle.api.Lifecycle;
import nl.positor.modularity.lifecycle.api.Restarter;
import nl.positor.modularity.lifecycle.api.ReverseDependencyLookup;

import java.util.*;
import java.util.stream.Collectors;

public class DefaultDependencyGraph implements DependencyGraph, DependencyLookup, ReverseDependencyLookup {
    private Restarter restarter;
    private Map<String, LifecycleComponent> componentMap;
    private Map<Component, List<Dependency>> dependencyLookup;
    private Map<Component, List<Dependency>> reverseDependencyLookup;

    DefaultDependencyGraph(Restarter restarter, Map<String, LifecycleComponent> componentMap, Map<Component, List<Dependency>> dependencyLookup) {
        this.restarter = restarter;
        this.componentMap = Collections.unmodifiableMap(new HashMap<>(componentMap));
        this.dependencyLookup = Collections.unmodifiableMap(new HashMap<>(dependencyLookup));
        Map<Component, List<Dependency>> reverseLookupWorkingMap = new HashMap<>();
        for (Map.Entry<Component, List<Dependency>> entry : dependencyLookup.entrySet()) {
            Dependency reverseDependency = new DefaultDependency(entry.getKey().getName());
            entry.getValue().stream().map(d -> componentMap.get(d.getTargetName()))
                    .forEach(
                            lifecycleComponent -> reverseLookupWorkingMap.computeIfAbsent(lifecycleComponent, l -> new ArrayList<>()).add(reverseDependency)
                    );
        }
        this.reverseDependencyLookup = Collections.unmodifiableMap(reverseLookupWorkingMap);
    }

    @Override
    public void startAll() {
        Lifecycle[] lifecycleObjects = componentMap.values().toArray(new Lifecycle[componentMap.size()]);
        restarter.start(this, lifecycleObjects);
    }

    @Override
    public Component getComponentByName(String name) {
        return componentMap.get(name);
    }

    @Override
    public void stopAll() {
        Lifecycle[] lifecycleObjects = componentMap.values().toArray(new Lifecycle[componentMap.size()]);
        restarter.stop(this, lifecycleObjects);
    }

    @Override
    public Collection<? extends Lifecycle> getDependencies(Lifecycle lifecycleObject) {
        return dependencyLookup.getOrDefault(lifecycleObject, Collections.emptyList()).stream().map(d -> componentMap.get(d.getTargetName())).collect(Collectors.toList());
    }

    @Override
    public Collection<? extends Lifecycle> getDependants(Lifecycle lifecycleObject) {
        return reverseDependencyLookup.getOrDefault(lifecycleObject, Collections.emptyList()).stream().map(d -> componentMap.get(d.getTargetName())).collect(Collectors.toList());
    }
}
