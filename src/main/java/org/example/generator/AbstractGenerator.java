package org.example.generator;

import java.lang.reflect.Modifier;
import org.example.generator.registry.GeneratorRegistry;

public class AbstractGenerator extends BaseGenerator {
    private final GeneratorRegistry registry;

    public AbstractGenerator(GeneratorRegistry registry) {
        this.registry = registry;
    }

    @Override
    public <T> T generateValueOfType(Class<T> clazz) {
        if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
            Class<?> implementation = registry.getRandomImplementingClass(clazz);
            return clazz.cast(reGenerateValue(implementation));
        }

        return generateWithNext(clazz);
    }
}
