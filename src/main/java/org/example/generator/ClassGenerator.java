package org.example.generator;

public class ClassGenerator extends BaseGenerator {
    @Override
    public <T> T generateValueOfType(Class<T> clazz) {
        // TODO implement
        return generateWithNext(clazz);
    }
}
