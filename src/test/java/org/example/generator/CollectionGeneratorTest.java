package org.example.generator;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.SequencedCollection;
import java.util.Set;
import java.util.SortedSet;
import java.util.Vector;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TransferQueue;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CollectionGeneratorTest {
    private final Generator generator = GeneratorTestSupport.generator();

    @ParameterizedTest
    @MethodSource("supportedCollectionInterfaces")
    void generatesNonEmptyStringCollections(Class<?> collectionType) {
        Object value = generator.generateValueOfType(collectionType);
        assertInstanceOf(Collection.class, value);
        Collection<?> collection = (Collection<?>) value;
        assertFalse(collection.isEmpty());
        assertTrue(collection.stream().allMatch(String.class::isInstance));
    }

    @ParameterizedTest
    @MethodSource("concreteCollections")
    void instantiatesConcreteCollectionTypesWithNoArgumentConstructors(Class<? extends Collection> type) {
        Object value = generator.generateValueOfType(type);
        assertInstanceOf(type, value);
        assertFalse(((Collection<?>) value).isEmpty());
    }

    @org.junit.jupiter.api.Test
    void rejectsCollectionSubinterfacesWithoutDefaultImplementations() {
        assertThrows(
            IllegalArgumentException.class,
            () -> generator.generateValueOfType(CustomCollection.class)
        );
    }

    private static Stream<Class<?>> supportedCollectionInterfaces() {
        return Stream.of(
            Collection.class, SequencedCollection.class, List.class, Set.class,
            SortedSet.class, NavigableSet.class, Queue.class, Deque.class,
            BlockingQueue.class, BlockingDeque.class, TransferQueue.class
        );
    }

    private static Stream<Class<? extends Collection>> concreteCollections() {
        return Stream.of(LinkedList.class, Vector.class);
    }

    interface CustomCollection extends Collection<String> {
    }
}
