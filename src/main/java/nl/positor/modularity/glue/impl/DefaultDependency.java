package nl.positor.modularity.glue.impl;

import nl.positor.modularity.glue.api.Dependency;

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
