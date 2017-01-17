package nl.positor.modularity.glue.api;

/**
 * Created by Arien on 17-Dec-16.
 */
public interface DependencyGraph {

    void startAll();

    Component getComponentByName(String name);

    void stopAll();
}
