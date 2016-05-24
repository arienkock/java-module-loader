package nl.positor.module.loading;

/**
 * Instantiates an object for use as a persistent application module.
 *
 * <p>
 *     The object is cached until {@link #reload()} is called, after which the module is reinstantiated.
 * </p>
 * Created by Arien on 24-May-16.
 */
public interface ModuleLoader {
    Object get() throws Exception;
    Object reload() throws Exception;

}
