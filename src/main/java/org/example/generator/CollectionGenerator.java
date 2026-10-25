package org.example.generator;

public class CollectionGenerator extends BaseGenerator {
    private static final int SIZE = 5;

    @Override
    public <T> T generateValueOfType(Class<T> clazz) {
        // TODO implement
        return generateWithNext(clazz);
    }
}
