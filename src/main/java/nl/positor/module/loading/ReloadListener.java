package nl.positor.module.loading;

/**
 * Created by Arien on 25-May-16.
 */
public interface ReloadListener {
    void beforeReload(Object oldInstance) throws Exception;
    void afterReload() throws Exception;
}
