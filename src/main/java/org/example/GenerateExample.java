package org.example;


import java.lang.reflect.Modifier;
import java.util.List;
import org.example.generator.AbstractGenerator;
import org.example.generator.ArrayGenerator;
import org.example.generator.ClassGenerator;
import org.example.generator.CollectionGenerator;
import org.example.generator.EnumGenerator;
import org.example.generator.Generator;
import org.example.generator.MapGenerator;
import org.example.generator.PrimitiveGenerator;
import org.example.generator.RecursiveGenerator;
import org.example.generator.StringGenerator;
import org.example.generator.registry.Generatable;
import org.example.generator.registry.GeneratorRegistry;

public class GenerateExample {
    private static final Generator GENERATOR =
        new RecursiveGenerator()
            .setNext(new PrimitiveGenerator())
            .setNext(new StringGenerator())
            .setNext(new ArrayGenerator())
            .setNext(new EnumGenerator())
            .setNext(new CollectionGenerator())
            .setNext(new MapGenerator())
            .setNext(new AbstractGenerator(
                new GeneratorRegistry("org.example.classes")))
            .setNext(new ClassGenerator())
            .getRoot();

    public static void main(String[] args) {
        record Letter(String name) {}

        System.out.println(
            Modifier.isAbstract(Generatable.class.getModifiers())
        );

        List x = GENERATOR.generateValueOfType(List.class);
        System.out.println(x);
    }
}