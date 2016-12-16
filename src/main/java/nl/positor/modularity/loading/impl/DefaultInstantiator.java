package nl.positor.modularity.loading.impl;

import nl.positor.modularity.loading.api.InstanceHolder;
import nl.positor.modularity.loading.api.Instantiator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Arien on 16-Dec-16.
 */
public class DefaultInstantiator implements Instantiator {
    private final String className;
    private final InstanceHolder[] constructorArguments;
    private final Map<String, InstanceHolder[]> methodParametersMap;

    public DefaultInstantiator(String className, InstanceHolder[] constructorArguments, Map<String, InstanceHolder[]> methodParametersMap) {
        Objects.requireNonNull(className, "Class name is required");
        Objects.requireNonNull(constructorArguments, "A non-null value for constructor arguments is required");
        Objects.requireNonNull(methodParametersMap, "A non-null method arguments map is required");
        this.className = className;
        this.constructorArguments = constructorArguments;
        this.methodParametersMap = methodParametersMap;
    }

    @Override
    public Object create(ClassLoader classLoader) {
        Class<?> loadedClass = loadClass(classLoader);
        Object instance = construct(loadedClass);
        callMethods(loadedClass, instance);
        return instance;
    }

    private void callMethods(Class<?> loadedClass, Object instance) {
        outerMethodLoop:
        for (Map.Entry<String, InstanceHolder[]> entry : methodParametersMap.entrySet()) {
            String methodName = entry.getKey();
            InstanceHolder[] params = entry.getValue();
            int methodArgLength = params.length;
            final Object[] requiredMethodArgs = new Object[methodArgLength];
            for (int argIdx = 0; argIdx < params.length; argIdx++) {
                requiredMethodArgs[argIdx] = params[argIdx].get();
            }
            for (Method method : loadedClass.getMethods()) {
                // TODO: add args type checking to method call
                if (method.getName().equals(methodName) && method.getParameterCount() == methodArgLength) {
                    try {
                        method.invoke(instance, requiredMethodArgs);
                        continue outerMethodLoop;
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new IllegalArgumentException(e);
                    }
                }
            }
            throw new IllegalArgumentException("No suitable method found for name " + methodName + " and num args " + methodArgLength);
        }
    }

    private Object construct(Class<?> loadedClass) {
        Object instance = null;
        final int cArgLength = constructorArguments.length;
        final Object[] requiredInstanceArgs = new Object[cArgLength];
        for (int argIdx = 0; argIdx < constructorArguments.length; argIdx++) {
            requiredInstanceArgs[argIdx] = constructorArguments[argIdx].get();
        }
        for (Constructor<?> constructor : loadedClass.getConstructors()) {
            // TODO: add args type checking to reflective instantiation
            if (constructor.getParameterCount() == cArgLength) {
                try {
                    instance = constructor.newInstance(requiredInstanceArgs);
                    break;
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }
        if (instance == null) {
            throw new IllegalArgumentException("No suitable constructor found");
        }
        return instance;
    }

    private Class<?> loadClass(ClassLoader classLoader) {
        Class<?> loadedClass;
        try {
            loadedClass = classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("The provided classloader cannot load class with name " + className, e);
        }
        return loadedClass;
    }
}
