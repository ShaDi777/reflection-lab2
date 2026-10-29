package org.example.generator;

import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NavigableSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class CollectionGenerator extends BaseGenerator {
    private static final int SIZE = 5;
    private static final List<Class<? extends Collection>> DEFAULT_IMPLEMENTATIONS = List.of(
        ArrayList.class,
        LinkedHashSet.class,
        TreeSet.class,
        ArrayDeque.class,
        PriorityQueue.class
    );

    @Override
    public <T> T generateValueOfType(Class<T> clazz) {
        if (!Collection.class.isAssignableFrom(clazz)) {
            return generateWithNext(clazz);
        }

        Collection<Object> collection = createCollection(clazz);
        for (int i = 0; i < SIZE; i++) {
            String value = reGenerateValue(String.class) + "-" + i;
            collection.add(value);
        }
        return clazz.cast(collection);
    }

    @SuppressWarnings("unchecked")
    private static Collection<Object> createCollection(Class<?> clazz) {
        if (!clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())) {
            try {
                return (Collection<Object>) clazz.getDeclaredConstructor().newInstance();
            } catch (ReflectiveOperationException exception) {
                throw new IllegalArgumentException(
                    "Cannot create collection " + clazz.getTypeName()
                        + ": a usable no-argument constructor is required",
                    exception
                );
            }
        }

        Class<? extends Collection> defaultImplementation;
        if (clazz == Collection.class || clazz == List.class) {
            defaultImplementation = ArrayList.class;
        } else if (clazz == Set.class) {
            defaultImplementation = LinkedHashSet.class;
        } else if (clazz == SortedSet.class || clazz == NavigableSet.class) {
            defaultImplementation = TreeSet.class;
        } else if (clazz == Queue.class) {
            defaultImplementation = ArrayDeque.class;
        } else if (clazz == Deque.class) {
            defaultImplementation = ArrayDeque.class;
        } else {
            defaultImplementation = DEFAULT_IMPLEMENTATIONS.stream()
                                                           .filter(clazz::isAssignableFrom)
                                                           .findFirst()
                                                           .orElseThrow(() -> new IllegalArgumentException(
                                                               "No default collection implementation for " +
                                                                   clazz.getTypeName()));
        }

        try {
            return (Collection<Object>) defaultImplementation.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(
                "Cannot create default collection "
                    + defaultImplementation.getTypeName(),
                exception
            );
        }
    }
}
