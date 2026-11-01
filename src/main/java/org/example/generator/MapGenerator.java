package org.example.generator;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class MapGenerator extends BaseGenerator {
    private static final int SIZE = 5;
    private static final List<Class<? extends Map>> DEFAULT_IMPLEMENTATIONS = List.of(
        HashMap.class,
        LinkedHashMap.class,
        TreeMap.class,
        ConcurrentHashMap.class,
        ConcurrentSkipListMap.class,
        Hashtable.class,
        IdentityHashMap.class
    );

    @Override
    public <T> T generateValueOfType(Class<T> clazz) {
        if (!Map.class.isAssignableFrom(clazz)) {
            return generateWithNext(clazz);
        }

        Map<Object, Object> map = createMap(clazz);
        fillMap(map, String.class, String.class);
        return clazz.cast(map);
    }

    @Override
    public Object generateValueOfType(Type type) {
        if (!(type instanceof ParameterizedType parameterizedType)
                || !(parameterizedType.getRawType() instanceof Class<?> rawClass)
                || !Map.class.isAssignableFrom(rawClass)) {
            return super.generateValueOfType(type);
        }

        Type[] arguments = parameterizedType.getActualTypeArguments();
        Map<Object, Object> map = createMap(rawClass);
        fillMap(map, arguments[0], arguments[1]);
        return map;
    }

    private void fillMap(Map<Object, Object> map, Type keyType, Type valueType) {
        for (int i = 0; i < SIZE; i++) {
            Object key = reGenerateValue(keyType);
            Object value = reGenerateValue(valueType);
            map.put(key, value);
        }
    }

    private static Map<Object, Object> createMap(Class<?> clazz) {
        if (!clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())) {
            return emptyMap(clazz);
        }

        Class<?> implementation = DEFAULT_IMPLEMENTATIONS
                                      .stream()
                                      .filter(clazz::isAssignableFrom)
                                      .findAny()
                                      .orElseThrow(() -> new IllegalArgumentException(
                                          "No default map implementation for " + clazz.getTypeName()));

        return emptyMap(implementation);
    }

    @SuppressWarnings("unchecked")
    private static Map<Object, Object> emptyMap(Class<?> implClass) {
        try {
            return (Map<Object, Object>) implClass.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(
                "Cannot create empty map " + implClass.getTypeName(),
                exception
            );
        }
    }
}
