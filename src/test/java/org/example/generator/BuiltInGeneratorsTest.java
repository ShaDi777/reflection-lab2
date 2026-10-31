package org.example.generator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.Set;
import java.util.SequencedCollection;
import java.util.SequencedMap;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.TransferQueue;
import org.example.generator.registry.GeneratorRegistry;
import org.junit.jupiter.api.Test;

class BuiltInGeneratorsTest {
    private final Generator generator = new RecursiveGenerator()
        .setNext(new PrimitiveGenerator())
        .setNext(new StringGenerator())
        .setNext(new ArrayGenerator())
        .setNext(new EnumGenerator())
        .setNext(new CollectionGenerator())
        .setNext(new MapGenerator())
        .setNext(new AbstractGenerator(new GeneratorRegistry("org.example.classes")))
        .setNext(new ClassGenerator())
        .getRoot();

    @Test
    void generatesNonEmptyBasicCollectionInterfaces() {
        assertNonEmpty(generator.generateValueOfType(Collection.class));
        assertNonEmpty(generator.generateValueOfType(SequencedCollection.class));
        assertNonEmpty(generator.generateValueOfType(List.class));
        assertNonEmpty(generator.generateValueOfType(Set.class));
        assertNonEmpty(generator.generateValueOfType(SortedSet.class));
        assertNonEmpty(generator.generateValueOfType(NavigableSet.class));
        assertNonEmpty(generator.generateValueOfType(Queue.class));
        assertNonEmpty(generator.generateValueOfType(Deque.class));
        assertNonEmpty(generator.generateValueOfType(BlockingQueue.class));
        assertNonEmpty(generator.generateValueOfType(BlockingDeque.class));
        assertNonEmpty(generator.generateValueOfType(TransferQueue.class));
    }

    @Test
    void generatesNonEmptyMapInterfaces() {
        assertNonEmpty(generator.generateValueOfType(Map.class));
        assertNonEmpty(generator.generateValueOfType(SortedMap.class));
        assertNonEmpty(generator.generateValueOfType(NavigableMap.class));
        assertNonEmpty(generator.generateValueOfType(SequencedMap.class));
        assertNonEmpty(generator.generateValueOfType(ConcurrentMap.class));
        assertNonEmpty(generator.generateValueOfType(ConcurrentNavigableMap.class));
    }

    @Test
    void generatesNonEmptyStrings() {
        String value = generator.generateValueOfType(String.class);
        assertNotNull(value);
        assertFalse(value.isEmpty());
        assertTrue(value.matches("[a-zA-Z0-9]+"));
    }

    private static void assertNonEmpty(Collection<?> collection) {
        assertNotNull(collection);
        assertFalse(collection.isEmpty());
        assertTrue(collection.stream().allMatch(value -> value instanceof String));
    }

    private static void assertNonEmpty(Map<?, ?> map) {
        assertNotNull(map);
        assertFalse(map.isEmpty());
        assertTrue(map.entrySet().stream()
            .allMatch(entry -> entry.getKey() instanceof String && entry.getValue() instanceof String));
    }
}
