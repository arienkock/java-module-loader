package nl.positor.modularity.loading.impl;

import nl.positor.modularity.loading.api.InstanceProvider;
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
    private final InstanceProvider[] constructorArguments;
    private final Map<String, InstanceProvider[]> methodParametersMap;

    public DefaultInstantiator(String className, InstanceProvider[] constructorArguments, Map<String, InstanceProvider[]> methodParametersMap) {
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
        for (Map.Entry<String, InstanceProvider[]> entry : methodParametersMap.entrySet()) {
            String methodName = entry.getKey();
            InstanceProvider[] params = entry.getValue();
            int methodArgLength = params.length;
            final Object[] requiredMethodArgs = new Object[methodArgLength];
            for (int argIdx = 0; argIdx < params.length; argIdx++) {
                requiredMethodArgs[argIdx] = params[argIdx].get();
            }
            for (Method method : loadedClass.getMethods()) {
                if (method.getName().equals(methodName) && method.getParameterCount() == methodArgLength && paramsMatch(method.getParameterTypes(), requiredMethodArgs)) {
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

    private boolean paramsMatch(Class<?>[] parameterTypes, Object[] requiredMethodArgs) {
        for (int i = 0; i < parameterTypes.length; i++) {
            if (!parameterTypes[i].isAssignableFrom(requiredMethodArgs[i].getClass())) {
                return false;
            }
        }
        return true;
    }

    private Object construct(Class<?> loadedClass) {
        Object instance = null;
        final int cArgLength = constructorArguments.length;
        final Object[] requiredInstanceArgs = new Object[cArgLength];
        for (int argIdx = 0; argIdx < constructorArguments.length; argIdx++) {
            requiredInstanceArgs[argIdx] = constructorArguments[argIdx].get();
        }
        for (Constructor<?> constructor : loadedClass.getConstructors()) {
            if (constructor.getParameterCount() == cArgLength && paramsMatch(constructor.getParameterTypes(), requiredInstanceArgs)) {
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
