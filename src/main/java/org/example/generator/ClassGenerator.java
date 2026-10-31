package org.example.generator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

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
                    arguments[i] = generateThroughChain(parameterTypes[i]);
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

    private Object generateThroughChain(Type type) {
        if (type instanceof Class<?> valueClass) {
            return reGenerateValue(valueClass);
        }
        if (type instanceof ParameterizedType parameterizedType
                && parameterizedType.getRawType() instanceof Class<?> rawClass) {
            Object value = reGenerateValue(rawClass);
            Type[] arguments = parameterizedType.getActualTypeArguments();
            if (value instanceof Collection<?> collection && arguments.length == 1) {
                @SuppressWarnings("unchecked")
                Collection<Object> values = (Collection<Object>) collection;
                values.clear();
                for (int i = 0; i < 5; i++) {
                    values.add(generateThroughChain(arguments[0]));
                }
            } else if (value instanceof Map<?, ?> map && arguments.length == 2) {
                @SuppressWarnings("unchecked")
                Map<Object, Object> values = (Map<Object, Object>) map;
                values.clear();
                for (int i = 0; i < 5; i++) {
                    values.put(generateThroughChain(arguments[0]), generateThroughChain(arguments[1]));
                }
            }
            return value;
        }
        if (type instanceof WildcardType wildcardType) {
            Type[] upperBounds = wildcardType.getUpperBounds();
            return generateThroughChain(upperBounds.length == 0 || upperBounds[0] == Object.class
                                        ? String.class : upperBounds[0]);
        }
        return reGenerateValue(String.class);
    }
}
