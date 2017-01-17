package nl.positor.modularity.glue.impl.component;

import nl.positor.modularity.glue.api.component.Dependency;

/**
 * Created by Arien on 28-Dec-16.
 */
public class DefaultDependency implements Dependency {
    private String targetName;

    public DefaultDependency(String name) {
        targetName = name;
    }

    @Override
    public String getTargetName() {
        return targetName;
    }
}
