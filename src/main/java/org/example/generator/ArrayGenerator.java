package org.example.generator;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

public class ArrayGenerator extends BaseGenerator {
    private final int minSize;
    private final int maxSize;

    public ArrayGenerator() {
        this(3, 10);
    }

    public ArrayGenerator(int lowerBound, int upperBound) {
        if (lowerBound <= 0 || upperBound < lowerBound || upperBound == Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Array bounds must be 0 < lowerBound <= upperBound < Integer.MAX_VALUE");
        }
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
            return clazz.cast(array);
        }

        return generateWithNext(clazz);
    }

    @Override
    public Object generateValueOfType(Type type) {
        if (!(type instanceof GenericArrayType arrayType)) {
            return super.generateValueOfType(type);
        }

        Type componentType = arrayType.getGenericComponentType();
        Class<?> componentClass = getArrayComponentClass(componentType);
        int length = RANDOM.nextInt(minSize, maxSize + 1);
        Object array = Array.newInstance(componentClass, length);
        for (int i = 0; i < length; i++) {
            Array.set(array, i, reGenerateValue(componentType));
        }
        return array;
    }

    private static Class<?> getArrayComponentClass(Type componentType) {
        return switch (componentType) {
            case Class<?> componentClass -> componentClass;

            case ParameterizedType parameterizedType
                when parameterizedType.getRawType() instanceof Class<?> rawClass -> rawClass;

            case GenericArrayType genericArrayType ->
                Array.newInstance(getArrayComponentClass(genericArrayType.getGenericComponentType()), 0).getClass();

            case WildcardType wildcardType -> getArrayComponentClass(firstBoundOrObject(wildcardType.getUpperBounds()));

            case TypeVariable<?> typeVariable -> getArrayComponentClass(firstBoundOrObject(typeVariable.getBounds()));

            default -> throw new IllegalArgumentException("Cannot determine array component type for " + componentType);
        };
    }
}
