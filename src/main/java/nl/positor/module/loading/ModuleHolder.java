package nl.positor.module.loading;

/**
 * Created by Arien on 26-May-16.
 */
public interface ModuleHolder {

    /**
     * Returns the logical name of the module <strong>instance</strong>, as opposed to the
     * {@link nl.positor.module.definition.ModuleDefinition}
     */
    String getName();

    /**
     * If the module has a main entrypoint, ensures underlying module is loaded and returns the instance.
     * Otherwise the classes are simply made available to be used by dependent modules.
     *
     * @return either the main entrypoint to the module or null if not applicable
     */
    Object get();

    ModuleHolder reset();

    static ModuleHolder holderFor(String name, ModuleLoaderOnly moduleLoader) {
        return new ModuleHolder() {
            Object instance;

            @Override
            public String getName() {
                return name;
            }

            @Override
            public Object get() {
                if (instance == null) {
                    instance = moduleLoader.load();
                }
                return instance;
            }

            @Override
            public ModuleHolder reset() {
                instance = null;
                return this;
            }
        };
    }
}
