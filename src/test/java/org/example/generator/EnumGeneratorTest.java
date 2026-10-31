package org.example.generator;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.example.classes.Letter;
import org.junit.jupiter.api.Test;

class EnumGeneratorTest {
    private final Generator generator = GeneratorTestSupport.generator();

    @Test
    void generatesAValueFromEnumConstants() {
        Letter value = generator.generateValueOfType(Letter.class);
        assertNotNull(value);
        assertInstanceOf(Letter.class, value);
    }

    @Test
    void rejectsEnumsWithoutConstants() {
        assertThrows(IllegalArgumentException.class, () -> generator.generateValueOfType(EmptyEnum.class));
    }

    enum EmptyEnum {
    }
}
