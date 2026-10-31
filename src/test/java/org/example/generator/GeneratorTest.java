package org.example.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.example.classes.BinaryTreeNode;
import org.example.classes.Cart;
import org.example.classes.Example;
import org.example.classes.Letter;
import org.example.classes.Product;
import org.example.classes.Rectangle;
import org.example.classes.Shape;
import org.example.classes.Triangle;
import org.example.generator.registry.Generatable;
import org.example.generator.registry.GeneratorRegistry;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeneratorTest {
    private final Generator generator =
        new RecursiveGenerator()
            .setNext(new PrimitiveGenerator())
            .setNext(new StringGenerator())
            .setNext(new ArrayGenerator())
            .setNext(new EnumGenerator())
            .setNext(new CollectionGenerator())
            .setNext(new MapGenerator())
            .setNext(new AbstractGenerator(new GeneratorRegistry(
                "org.example.classes", ArrayList.class, TestAbstractBase.class, TestAbstractImplementation.class
            )))
            .setNext(new ClassGenerator())
            .getRoot();

    @Test
    void generatesPrimitiveAndWrapperValues() {
        assertNotNull(generator.generateValueOfType(byte.class));
        assertNotNull(generator.generateValueOfType(Byte.class));
        assertNotNull(generator.generateValueOfType(short.class));
        assertNotNull(generator.generateValueOfType(Short.class));
        assertNotNull(generator.generateValueOfType(int.class));
        assertNotNull(generator.generateValueOfType(Integer.class));
        assertNotNull(generator.generateValueOfType(long.class));
        assertNotNull(generator.generateValueOfType(Long.class));
        assertNotNull(generator.generateValueOfType(float.class));
        assertNotNull(generator.generateValueOfType(Float.class));
        assertNotNull(generator.generateValueOfType(double.class));
        assertNotNull(generator.generateValueOfType(Double.class));
        assertNotNull(generator.generateValueOfType(char.class));
        assertNotNull(generator.generateValueOfType(Character.class));
        assertNotNull(generator.generateValueOfType(boolean.class));
        assertNotNull(generator.generateValueOfType(Boolean.class));
        assertThrows(IllegalArgumentException.class, () -> generator.generateValueOfType(void.class));
        assertThrows(IllegalArgumentException.class, () -> generator.generateValueOfType(Void.class));
    }

    @Test
    void generatesNonEmptyArraysWithSupportedComponents() {
        int[] numbers = generator.generateValueOfType(int[].class);
        String[] strings = generator.generateValueOfType(String[].class);
        Letter[] letters = generator.generateValueOfType(Letter[].class);
        Rectangle[] rectangles = generator.generateValueOfType(Rectangle[].class);
        Integer[][] matrix = generator.generateValueOfType(Integer[][].class);

        assertTrue(numbers.length > 0);
        assertTrue(strings.length > 0 && List.of(strings).stream().allMatch(s -> s != null && !s.isEmpty()));
        assertTrue(letters.length > 0 && List.of(letters).stream().allMatch(letter -> letter != null));
        assertTrue(rectangles.length > 0 && List.of(rectangles).stream().allMatch(rectangle -> rectangle != null));
        assertTrue(matrix.length > 0 && List.of(matrix).stream().allMatch(row -> row != null && row.length > 0));
        assertThrows(IllegalArgumentException.class, () -> new ArrayGenerator(0, 3));
        assertThrows(IllegalArgumentException.class, () -> new ArrayGenerator(4, 3));
        assertThrows(
            IllegalArgumentException.class,
            () -> new ArrayGenerator(1, Integer.MAX_VALUE)
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> generator.generateValueOfType(Runnable[].class)
        );
    }

    @Test
    void generatesEnumsAndRejectsEmptyEnums() {
        assertNotNull(generator.generateValueOfType(Letter.class));
        assertThrows(IllegalArgumentException.class, () -> generator.generateValueOfType(EmptyEnum.class));
    }

    @Test
    void generatesUserObjectsAndInterfaceImplementations() {
        assertNotNull(generator.generateValueOfType(Example.class));
        assertNotNull(generator.generateValueOfType(Product.class).getName());
        assertNotNull(generator.generateValueOfType(Cart.class).getItems());
        assertInstanceOf(Shape.class, generator.generateValueOfType(Shape.class));
        assertInstanceOf(Shape.class, generator.generateValueOfType(Rectangle.class));
        assertInstanceOf(Shape.class, generator.generateValueOfType(Triangle.class));
        assertInstanceOf(
            TestAbstractImplementation.class,
            generator.generateValueOfType(TestAbstractBase.class)
        );
    }

    @Test
    void honorsGenericConstructorTypes() {
        Cart cart = generator.generateValueOfType(Cart.class);
        assertInstanceOf(ArrayList.class, cart.getItems());
        assertFalse(cart.getItems().isEmpty());
        assertTrue(cart.getItems().stream().allMatch(Product.class::isInstance));

        TypedMapHolder holder = generator.generateValueOfType(TypedMapHolder.class);
        assertFalse(holder.products.isEmpty());
        assertTrue(holder.products.keySet().stream().allMatch(String.class::isInstance));
        assertTrue(holder.products.values().stream().allMatch(Product.class::isInstance));
    }

    @Test
    void boundsRecursiveObjectGeneration() {
        for (int i = 0; i < 10; i++) {
            BinaryTreeNode tree = generator.generateValueOfType(BinaryTreeNode.class);
            assertNotNull(tree.getData());
            assertNotNull(tree.getLeft());
            assertNotNull(tree.getRight());
            assertNotNull(tree.getLeft().getData());
            assertNotNull(tree.getRight().getData());
            assertTrue(tree.getLeft().getLeft() == null && tree.getLeft().getRight() == null);
            assertTrue(tree.getRight().getLeft() == null && tree.getRight().getRight() == null);
        }
    }

    @Test
    void instantiatesTypesWithOnlyNoArgumentConstructors() {
        MutableFields value = generator.generateValueOfType(MutableFields.class);
        assertNotNull(value);
        assertTrue(value.name == null);
        assertTrue(value.count == 0);
        assertTrue(value.products == null);
    }

    @Test
    void reportsUnsupportedTypesClearly() {
        IllegalArgumentException noImplementation =
            assertThrows(IllegalArgumentException.class, () -> generator.generateValueOfType(Runnable.class));
        assertTrue(noImplementation.getMessage().contains(Runnable.class.getTypeName()));
        IllegalArgumentException noConstructor =
            assertThrows(IllegalArgumentException.class, () -> generator.generateValueOfType(Unconstructable.class));
        assertTrue(noConstructor.getMessage().contains("constructors failed"));
        assertDoesNotThrow(() -> generator.generateValueOfType(Integer.class));
    }

    enum EmptyEnum {
    }

    public static class MutableFields {
        private String name;
        private int count;
        private List<Product> products;

        public MutableFields() {
        }
    }

    public static class TypedMapHolder {
        private final Map<String, Product> products;

        public TypedMapHolder(Map<String, Product> products) {
            this.products = products;
        }
    }

    public static class Unconstructable {
        private Unconstructable(String value) {
            throw new IllegalStateException(value);
        }
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
