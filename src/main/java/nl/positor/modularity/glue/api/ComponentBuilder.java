package nl.positor.modularity.glue.api;

import java.net.URL;

/**
 * Created by Arien on 17-Dec-16.
 */
public interface ComponentBuilder {
    ComponentBuilder named(String name);

    ComponentBuilder loadsFrom(URL[] classpath);

    ComponentBuilder exposes(String... classNames);

    BlueprintBuilder createdBy();

    Component build();
}
