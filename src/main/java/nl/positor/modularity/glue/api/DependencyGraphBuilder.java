package nl.positor.modularity.glue.api;

import nl.positor.modularity.glue.api.component.ComponentBuilder;
import nl.positor.modularity.glue.api.component.Dependency;

/**
 * Created by Arien on 17-Dec-16.
 */
public interface DependencyGraphBuilder {
    ComponentBuilder withComponent();

    Dependency dependencyNamed(String name);

    DependencyGraph build();
}
