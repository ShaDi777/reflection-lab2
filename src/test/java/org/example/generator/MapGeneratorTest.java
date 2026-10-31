package org.example.generator;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SequencedMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MapGeneratorTest {
    private final Generator generator = GeneratorTestSupport.generator();

    @ParameterizedTest
    @MethodSource("supportedMapInterfaces")
    void generatesNonEmptyStringEntries(Class<?> mapType) {
        Object value = generator.generateValueOfType(mapType);
        assertInstanceOf(Map.class, value);
        Map<?, ?> map = (Map<?, ?>) value;
        assertFalse(map.isEmpty());
        assertTrue(map.entrySet().stream()
                      .allMatch(entry -> entry.getKey() instanceof String && entry.getValue() instanceof String));
    }

    @ParameterizedTest
    @MethodSource("concreteMaps")
    void instantiatesConcreteMapTypesWithNoArgumentConstructors(Class<? extends Map> type) {
        Object value = generator.generateValueOfType(type);
        assertInstanceOf(type, value);
        assertFalse(((Map<?, ?>) value).isEmpty());
    }

    @Test
    void rejectsMapSubinterfacesWithoutDefaultImplementations() {
        assertThrows(IllegalArgumentException.class, () -> generator.generateValueOfType(CustomMap.class));
    }

    private static Stream<Class<?>> supportedMapInterfaces() {
        return Stream.of(
            Map.class,
            SortedMap.class,
            NavigableMap.class,
            SequencedMap.class,
            ConcurrentMap.class,
            ConcurrentNavigableMap.class
        );
    }

    private static Stream<Class<? extends Map>> concreteMaps() {
        return Stream.of(HashMap.class, TreeMap.class);
    }

    interface CustomMap extends Map<String, String> {
    }
}
