package nl.positor.module.lifecycle;

/**
 * Created by Arien on 26-May-16.
 */
public interface Reloader {
    void reload(Lifecycle lifecycleObject, Context context) throws CyclicDependencyException;
}
