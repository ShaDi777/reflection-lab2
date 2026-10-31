package org.example.generator;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PrimitiveGeneratorTest {
    private final Generator generator = GeneratorTestSupport.generator();

    @ParameterizedTest
    @MethodSource("primitiveAndWrapperTypes")
    void generatesPrimitiveAndWrapperValues(Class<?> requestedType, Class<?> boxedType) {
        Object value = generator.generateValueOfType(requestedType);

        assertInstanceOf(boxedType, value);
        if (value instanceof Character character) {
            assertTrue(character >= 'a' && character <= 'z');
        }
    }

    @Test
    void rejectsVoidTypes() {
        assertThrows(IllegalArgumentException.class, () -> generator.generateValueOfType(void.class));
        assertThrows(IllegalArgumentException.class, () -> generator.generateValueOfType(Void.class));
    }

    private static Stream<Arguments> primitiveAndWrapperTypes() {
        return Stream.of(
            Arguments.of(byte.class, Byte.class),
            Arguments.of(Byte.class, Byte.class),
            Arguments.of(short.class, Short.class),
            Arguments.of(Short.class, Short.class),
            Arguments.of(int.class, Integer.class),
            Arguments.of(Integer.class, Integer.class),
            Arguments.of(long.class, Long.class),
            Arguments.of(Long.class, Long.class),
            Arguments.of(float.class, Float.class),
            Arguments.of(Float.class, Float.class),
            Arguments.of(double.class, Double.class),
            Arguments.of(Double.class, Double.class),
            Arguments.of(char.class, Character.class),
            Arguments.of(Character.class, Character.class),
            Arguments.of(boolean.class, Boolean.class),
            Arguments.of(Boolean.class, Boolean.class)
        );
    }
}
