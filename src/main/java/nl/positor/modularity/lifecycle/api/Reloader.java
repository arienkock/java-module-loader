package nl.positor.modularity.lifecycle.api;

/**
 * Created by Arien on 26-May-16.
 */
@FunctionalInterface
public interface Reloader {
    void reload(Context context, Lifecycle... lifecycleObjects);
}
