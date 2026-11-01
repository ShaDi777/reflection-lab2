package org.example.generator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Comparator;

public class ClassGenerator extends BaseGenerator {
    @Override
    public <T> T generateValueOfType(Class<T> clazz) {
        return shouldSkip(clazz) ? generateWithNext(clazz) : instantiate(clazz);
    }

    private <T> boolean shouldSkip(Class<T> clazz) {
        return clazz.isPrimitive() ||
                   clazz.isInterface() ||
                   Modifier.isAbstract(clazz.getModifiers()) ||
                   clazz.isArray() ||
                   clazz.isEnum();
    }

    private <T> T instantiate(Class<T> clazz) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        Arrays.sort(
            constructors,
            Comparator.comparingInt((Constructor<?> constructor) -> constructor.getParameterCount()).reversed()
        );
        Throwable lastFailure = null;
        boolean hasAccessibleConstructor = false;
        for (Constructor<?> constructor : constructors) {
            if (!constructor.trySetAccessible()) {
                continue;
            }
            hasAccessibleConstructor = true;
            try {
                Type[] parameterTypes = constructor.getGenericParameterTypes();
                Object[] arguments = new Object[parameterTypes.length];
                for (int i = 0; i < parameterTypes.length; i++) {
                    arguments[i] = reGenerateValue(parameterTypes[i]);
                }
                return clazz.cast(constructor.newInstance(arguments));
            } catch (Exception exception) {
                lastFailure = exception;
            }
        }
        if (!hasAccessibleConstructor) {
            throw new IllegalArgumentException(
                "No accessible constructor for " + clazz.getTypeName()
            );
        }
        throw new IllegalArgumentException(
            "All accessible constructors failed for " + clazz.getTypeName(),
            lastFailure
        );
    }

}
