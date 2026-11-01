package org.example.generator;

import org.example.classes.Rectangle;
import org.example.classes.Shape;
import org.example.classes.Triangle;
import org.example.generator.registry.Generatable;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AbstractGeneratorTest {
    private final Generator generator = GeneratorTestSupport.generator(TestAbstractImplementation.class);

    @Test
    void returnsRegisteredConcreteImplementations() {
        assertInstanceOf(Shape.class, generator.generateValueOfType(Shape.class));
        assertInstanceOf(TestAbstractImplementation.class, generator.generateValueOfType(TestAbstractBase.class));
    }

    @Test
    void directlyGeneratesConcreteClassesWithoutRegistryLookup() {
        assertInstanceOf(Rectangle.class, generator.generateValueOfType(Rectangle.class));
        assertInstanceOf(Triangle.class, generator.generateValueOfType(Triangle.class));
    }

    @Test
    void explainsMissingImplementations() {
        Generator withoutTestImplementation = GeneratorTestSupport.generator();
        assertThrows(
            IllegalArgumentException.class,
            () -> withoutTestImplementation.generateValueOfType(TestAbstractBase.class)
        );
    }

    public abstract static class TestAbstractBase {
        public abstract int value();
    }

    @Generatable
    public static class TestAbstractImplementation extends TestAbstractBase {
        private final int value;

        public TestAbstractImplementation(int value) {
            this.value = value;
        }

        @Override
        public int value() {
            return value;
        }
    }
}
