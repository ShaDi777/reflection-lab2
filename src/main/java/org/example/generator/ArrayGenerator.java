package org.example.generator;

import java.lang.reflect.Array;

public class ArrayGenerator extends BaseGenerator {
    private final int minSize;
    private final int maxSize;

    public ArrayGenerator() {
        this(3, 10);
    }

    public ArrayGenerator(int lowerBound, int upperBound) {
        assert lowerBound <= upperBound : "lowerBound <= upperBound";
        this.minSize = lowerBound;
        this.maxSize = upperBound;
    }

    @Override
    public <T> T generateValueOfType(Class<T> clazz) {
        // Arrays T[]
        if (clazz.isArray()) {
            var componentType = clazz.getComponentType();
            int length = RANDOM.nextInt(minSize, maxSize + 1);
            Object array = Array.newInstance(componentType, length);
            for (int i = 0; i < length; i++) {
                Array.set(array, i, reGenerateValue(componentType));
            }
            return (T) array;
        }

        return generateWithNext(clazz);
    }
}
