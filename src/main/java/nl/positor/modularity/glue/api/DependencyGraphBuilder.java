package nl.positor.modularity.glue.api;

/**
 * Created by Arien on 17-Dec-16.
 */
public interface DependencyGraphBuilder {
    ComponentBuilder withComponent();

    Dependency constant(Object value);

    Dependency dependency(ComponentBuilder component);

    Dependency dependencyNamed(String name);

    DependencyGraph build();
}
