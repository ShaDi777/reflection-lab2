package org.example;


import java.lang.reflect.Modifier;
import org.example.generator.registry.Generatable;

public class GenerateExample {
    public static void main(String[] args) {
        record Letter(String name) {}

        System.out.println(
            Modifier.isAbstract(Generatable.class.getModifiers())
        );
    }
}