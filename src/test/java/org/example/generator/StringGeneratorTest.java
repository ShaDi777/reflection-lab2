package org.example.generator;

import org.junit.jupiter.api.RepeatedTest;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringGeneratorTest {
    private final Generator generator = GeneratorTestSupport.generator();

    @RepeatedTest(100)
    void generatesNonEmptyAlphanumericStringsWithVaryingValues() {
        String value = generator.generateValueOfType(String.class);
        assertFalse(value.isEmpty());
        assertTrue(value.length() >= 5 && value.length() <= 12);
        assertTrue(value.matches("[a-zA-Z0-9]+"));
    }
}
