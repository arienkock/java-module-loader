package nl.positor.module.loading;

/**
 * Created by Arien on 26-May-16.
 */
public interface ModuleClassReader {
    ClassLoader getPublicClassLoader();
    Class<?> loadPrivate(String name) throws ClassNotFoundException;
}
