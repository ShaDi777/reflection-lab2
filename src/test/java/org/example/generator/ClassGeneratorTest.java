package org.example.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.example.classes.Cart;
import org.example.classes.Complex;
import org.example.classes.Example;
import org.example.classes.Product;
import org.junit.jupiter.api.Test;

class ClassGeneratorTest {
    private final Generator generator = GeneratorTestSupport.generator();

    @Test
    void createsTemplateClassesUsingConstructorArguments() {
        assertNotNull(generator.generateValueOfType(Example.class));

        Product product = generator.generateValueOfType(Product.class);
        assertNotNull(product.getName());
        assertTrue(product.getPrice() >= 0);

        Cart cart = generator.generateValueOfType(Cart.class);
        assertInstanceOf(ArrayList.class, cart.getItems());
        assertFalse(cart.getItems().isEmpty());
        assertTrue(cart.getItems().stream().allMatch(Product.class::isInstance));
    }

    @Test
    void usesGenericMapConstructorParameterTypes() {
        TypedMapHolder holder = generator.generateValueOfType(TypedMapHolder.class);
        assertFalse(holder.products.isEmpty());
        assertTrue(holder.products.keySet().stream().allMatch(String.class::isInstance));
        assertTrue(holder.products.values().stream().allMatch(Product.class::isInstance));
    }

    @Test
    void generatesComplexWithNestedGenericArrays() {
        Complex<?> complex = generator.generateValueOfType(Complex.class);

        assertNotNull(complex.getField());
        assertFalse(complex.getField().isEmpty());
        assertTrue(complex.getField().values().stream().allMatch(lists -> !lists.isEmpty()));
        for (List<? extends Object[]> arrays : complex.getField().values()) {
            for (Object[] array : arrays) {
                assertNotNull(array);
                assertTrue(array.length > 0);
                assertTrue(java.util.Arrays.stream(array).allMatch(Object.class::isInstance));
            }
        }
        System.out.println(complex.getField());
    }

    @Test
    void routesNestedParameterizedTypesThroughTheChain() throws Exception {
        Type nestedType = NestedGenericHolder.class.getDeclaredField("products").getGenericType();

        Object value = generator.generateValueOfType(nestedType);

        assertInstanceOf(List.class, value);
        assertFalse(((List<?>) value).isEmpty());
        assertTrue(((List<?>) value).stream().allMatch(Map.class::isInstance));
        for (Object nestedMap : (List<?>) value) {
            Map<?, ?> products = (Map<?, ?>) nestedMap;
            assertFalse(products.isEmpty());
            assertTrue(products.keySet().stream().allMatch(String.class::isInstance));
            assertTrue(products.values().stream().allMatch(Product.class::isInstance));
        }
    }

    @Test
    void supportsNoArgumentConstructorsAndLeavesFieldsAtDefaults() {
        NoArgumentType value = generator.generateValueOfType(NoArgumentType.class);
        assertEquals(0, value.number);
        assertNull(value.name);
    }

    @Test
    void fallsBackToAnotherConstructorWhenOneThrows() {
        ConstructorFallback value = generator.generateValueOfType(ConstructorFallback.class);
        assertEquals("fallback", value.source);
    }

    @Test
    void reportsWhenAllConstructorsFail() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> generator.generateValueOfType(Unconstructable.class)
        );
        assertTrue(exception.getMessage().contains(Unconstructable.class.getTypeName()));
        assertTrue(exception.getMessage().contains("constructors failed"));
    }

    static class TypedMapHolder {
        private final Map<String, Product> products;

        TypedMapHolder(Map<String, Product> products) {
            this.products = products;
        }
    }

    static class NestedGenericHolder {
        private List<Map<String, Product>> products;
    }

    static class NoArgumentType {
        private int number;
        private String name;

        NoArgumentType() {
        }
    }

    static class ConstructorFallback {
        private final String source;

        ConstructorFallback(String value) {
            throw new IllegalStateException(value);
        }

        ConstructorFallback() {
            source = "fallback";
        }
    }

    static class Unconstructable {
        Unconstructable(String value) {
            throw new IllegalStateException(value);
        }
    }
}
