package org.example.generator;

import java.util.ArrayList;
import java.util.Arrays;
import org.example.classes.Letter;
import org.example.generator.registry.GeneratorRegistry;
import org.junit.jupiter.api.Test;

public class GeneratorTest {
    private GeneratorRegistry registry = new GeneratorRegistry("org.example.classes", ArrayList.class);
    private Generator generator = new PrimitiveGenerator()
                                      .setNext(new ArrayGenerator())
                                      .setNext(new EnumGenerator())
                                      .setNext(new CollectionGenerator())
                                      .setNext(new AbstractGenerator(registry))
                                      .setNext(new ClassGenerator())
                                      .getRoot();

    @Test
    public void primitiveInt() {
        Integer x = generator.generateValueOfType(Integer.class);
        int y = generator.generateValueOfType(int.class);
        System.out.println(x);
        System.out.println(y);
    }

    @Test
    public void arrayTest() {
        var x = generator.generateValueOfType(Integer[][].class);
        System.out.println(Arrays.deepToString(x));
    }

    @Test
    public void enumTest() {
        var x = generator.generateValueOfType(Letter.class);
        System.out.println(x);
    }
}
