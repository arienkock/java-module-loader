package nl.positor.module.loading;

import nl.positor.module.definition.ModuleDefinition;

import java.util.function.Function;

/**
 * Created by Arien on 27-May-16.
 */
public class ModuleLoaderWrapper implements ModuleLoaderOnly {
    private final Function<ModuleDefinition, Object> delegate;
    private final ModuleDefinition definition;

    public ModuleLoaderWrapper(ModuleDefinition definition, Function<ModuleDefinition, Object> delegate) {
        this.delegate = delegate;
        this.definition = definition;
    }

    @Override
    public Object load() {
        return delegate.apply(definition);
    }

    @Override
    public ModuleDefinition getDefinition() {
        return definition;
    }

    public static ModuleLoaderWrapper loaderFrom(ModuleDefinition definition, Function<ModuleDefinition, Object> delegate) {
        return new ModuleLoaderWrapper(definition, delegate);
    }

}
