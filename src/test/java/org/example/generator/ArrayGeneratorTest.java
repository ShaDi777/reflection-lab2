package org.example.generator;

import java.lang.reflect.Array;
import java.util.stream.Stream;
import org.example.classes.Letter;
import org.example.classes.Rectangle;
import org.example.classes.Shape;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArrayGeneratorTest {
    private final Generator generator = GeneratorTestSupport.generator();

    @ParameterizedTest
    @MethodSource("arrayTypes")
    void generatesNonEmptyArraysWithElementsOfRequestedComponentType(Class<?> arrayType) {
        Object array = generator.generateValueOfType(arrayType);
        Class<?> componentType = arrayType.getComponentType();

        assertTrue(Array.getLength(array) > 0);
        for (int i = 0; i < Array.getLength(array); i++) {
            Object value = Array.get(array, i);
            assertNotNull(value);
            if (!componentType.isArray()) {
                assertTrue(box(componentType).isInstance(value));
            } else {
                assertTrue(Array.getLength(value) > 0);
            }
        }
    }

    @Test
    void rejectsInvalidBounds() {
        assertThrows(IllegalArgumentException.class, () -> new ArrayGenerator(0, 3));
        assertThrows(IllegalArgumentException.class, () -> new ArrayGenerator(4, 3));
        assertThrows(IllegalArgumentException.class, () -> new ArrayGenerator(1, Integer.MAX_VALUE));
    }

    @Test
    void failsClearlyWhenComponentCannotBeGenerated() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> generator.generateValueOfType(Runnable[].class)
        );
        assertTrue(exception.getMessage().contains(Runnable.class.getTypeName()));
    }

    private static Stream<Class<?>> arrayTypes() {
        return Stream.of(
            int[].class, String[].class, Letter[].class, Rectangle[].class,
            double[][].class, Integer[][].class, Shape[][].class
        );
    }

    private static Class<?> box(Class<?> type) {
        if (!type.isPrimitive()) {
            return type;
        }
        return switch (type.getName()) {
            case "int" -> Integer.class;
            case "byte" -> Byte.class;
            case "short" -> Short.class;
            case "long" -> Long.class;
            case "float" -> Float.class;
            case "double" -> Double.class;
            case "char" -> Character.class;
            case "boolean" -> Boolean.class;
            default -> type;
        };
    }
}
