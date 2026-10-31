package org.example.generator;

import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class CollectionGenerator extends BaseGenerator {
    private static final int SIZE = 5;
    private static final List<Class<? extends Collection>> DEFAULT_IMPLEMENTATIONS = List.of(
        ArrayList.class,
        LinkedList.class,
        Vector.class,
        CopyOnWriteArrayList.class,
        LinkedHashSet.class,
        CopyOnWriteArraySet.class,
        TreeSet.class,
        ConcurrentSkipListSet.class,
        ArrayDeque.class,
        PriorityQueue.class,
        LinkedBlockingQueue.class,
        LinkedBlockingDeque.class,
        LinkedTransferQueue.class,
        PriorityBlockingQueue.class
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

    private static Collection<Object> createCollection(Class<?> clazz) {
        if (!clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())) {
            return emptyCollection(clazz);
        }

        Class<?> implementation = DEFAULT_IMPLEMENTATIONS
                                      .stream()
                                      .filter(clazz::isAssignableFrom)
                                      .findAny()
                                      .orElseThrow(() -> new IllegalArgumentException(
                                          "No default collection implementation for " +
                                              clazz.getTypeName()));
        return emptyCollection(implementation);
    }

    @SuppressWarnings("unchecked")
    private static Collection<Object> emptyCollection(Class<?> implementation) {
        try {
            return (Collection<Object>) implementation.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(
                "Cannot create empty collection " + implementation.getTypeName(),
                exception
            );
        }
    }
}
