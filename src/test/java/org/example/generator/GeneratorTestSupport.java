package org.example.generator;

import org.example.generator.registry.GeneratorRegistry;

final class GeneratorTestSupport {
    private GeneratorTestSupport() {
    }

    static Generator generator(Class<?>... additionalImplementations) {
        return new RecursiveGenerator()
            .setNext(new PrimitiveGenerator())
            .setNext(new StringGenerator())
            .setNext(new ArrayGenerator())
            .setNext(new EnumGenerator())
            .setNext(new CollectionGenerator())
            .setNext(new MapGenerator())
            .setNext(new AbstractGenerator(new GeneratorRegistry("org.example.classes", additionalImplementations)))
            .setNext(new ClassGenerator())
            .getRoot();
    }
}
