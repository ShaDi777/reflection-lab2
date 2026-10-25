package org.example.generator.registry;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GeneratorRegistry {
    private final List<Class<?>> implementations;

    public GeneratorRegistry(String basePackageName, Class<?>... otherSupportedClasses) {
        ArrayList<Class<?>> implementations = new ArrayList<>();
        // TODO scan base package
        //  get all Generatable classes
        //  append otherSupportedClasses
        //  filter out interfaces/abstract (only normal classes stay)
        this.implementations = implementations.stream().filter(GeneratorRegistry::isImplementable).toList();
    }

    public Class<?> getRandomImplementingClass(Class<?> clazz) {
        var validImplementations = getImplementingClasses(clazz);
        if (validImplementations.isEmpty()) {
            throw new IllegalArgumentException("No implementations in GeneratorRegistry for class " + clazz.getTypeName());
        }
        Collections.shuffle(validImplementations);
        return validImplementations.getFirst();
    }

    public List<Class<?>> getImplementingClasses(Class<?> clazz) {
        if (isImplementable(clazz)) {
            return List.of(clazz);
        }

        return implementations.stream().filter(clazz::isAssignableFrom).toList();
    }

    private static boolean isImplementable(Class<?> clazz) {
        return !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers());
    }
}
