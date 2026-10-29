package org.example.generator;

import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;

public class MapGenerator extends BaseGenerator {
    private static final int SIZE = 5;

    @Override
    public <T> T generateValueOfType(Class<T> clazz) {
        if (!Map.class.isAssignableFrom(clazz)) {
            return generateWithNext(clazz);
        }

        Map<Object, Object> map = createMap(clazz);
        for (int i = 0; i < SIZE; i++) {
            String key = reGenerateValue(String.class) + "-" + i;
            String value = reGenerateValue(String.class);
            map.put(key, value);
        }
        return clazz.cast(map);
    }

    @SuppressWarnings("unchecked")
    private static Map<Object, Object> createMap(Class<?> clazz) {
        if (!clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())) {
            try {
                return (Map<Object, Object>) clazz.getDeclaredConstructor().newInstance();
            } catch (ReflectiveOperationException exception) {
                throw new IllegalArgumentException("Cannot create map " + clazz.getTypeName()
                                                       + ": a usable no-argument constructor is required",
                                                   exception);
            }
        }

        Class<? extends Map> implementation;
        if (clazz == SortedMap.class || clazz == NavigableMap.class) {
            implementation = TreeMap.class;
        } else if (clazz == Map.class) {
            implementation = LinkedHashMap.class;
        } else if (clazz.isAssignableFrom(LinkedHashMap.class)) {
            implementation = LinkedHashMap.class;
        } else if (clazz.isAssignableFrom(TreeMap.class)) {
            implementation = TreeMap.class;
        } else {
            throw new IllegalArgumentException("No default map implementation for " + clazz.getTypeName());
        }

        try {
            return (Map<Object, Object>) implementation.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Cannot create default map " + implementation.getTypeName(),
                                            exception);
        }
    }
}
