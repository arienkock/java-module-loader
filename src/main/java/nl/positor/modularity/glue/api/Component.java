package nl.positor.modularity.glue.api;

/**
 * Created by Arien on 17-Dec-16.
 */
public interface Component extends Lifecycle {
    String getName();

    void reload();

    Object get();

    void release();
}
