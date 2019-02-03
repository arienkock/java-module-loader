package nl.positor.modularity.glue.impl.component;

import nl.positor.modularity.classpath.api.ModuleClassLoaderPair;
import nl.positor.modularity.loading.api.Instantiator;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

/**
 * Created by Arien on 13-Jan-17.
 */
public class DefaultComponent implements LifecycleComponent {

    private final Instantiator instantiator;
    private final String name;
    private final String startMethodName;
    private final String stopMethodName;
    private final Supplier<ModuleClassLoaderPair> classLoaderPairSupplier;
    private Object instance;
    private boolean started = false;

    public DefaultComponent(Instantiator instantiator, Supplier<ModuleClassLoaderPair> classLoaderPairSupplier, String name, String startMethodName, String stopMethodName) {
        this.instantiator = instantiator;
        this.classLoaderPairSupplier = classLoaderPairSupplier;
        this.name = name;
        this.startMethodName = startMethodName;
        this.stopMethodName = stopMethodName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getInstance() {
        if (instance == null) {
            this.instance = instantiator.create(classLoaderPairSupplier.get().getInnerClassLoader());
        }
        return instance;
    }


    @Override
    public boolean startIfStopped() {
        if (!started && startMethodName != null) {
            Object instance = getInstance();
            try {
                instance.getClass().getMethod(startMethodName).invoke(instance);
                started = true;
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
        return false;
    }

    @Override
    public void clean() {

    }

    @Override
    public boolean stopIfStarted() {
        if (started && stopMethodName != null) {
            Object instance = getInstance();
            try {
                instance.getClass().getMethod(stopMethodName).invoke(instance);
                started = false;
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
        return false;
    }
}
