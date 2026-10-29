package org.example.generator.registry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.example.classes.Rectangle;
import org.example.classes.Shape;
import org.example.classes.Triangle;
import org.junit.jupiter.api.Test;

class GeneratorRegistryTest {
    private final GeneratorRegistry registry = new GeneratorRegistry("org.example.classes");

    @Test
    void discoversOnlyGeneratableImplementations() {
        assertEquals(Set.of(Rectangle.class, Triangle.class),
                     Set.copyOf(registry.getImplementingClasses(Shape.class)));
    }

    @Test
    void choosesAnAssignableImplementation() {
        assertTrue(Shape.class.isAssignableFrom(registry.getRandomImplementingClass(Shape.class)));
    }
}
