package org.example.generator;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class RecursiveGenerator extends BaseGenerator {
    private static final int MAX_GENERATIONS_PER_TYPE = 2;

    private final ThreadLocal<Map<Class<?>, Integer>> activeTypes =
        ThreadLocal.withInitial(HashMap::new);

    @Override
    public <T> T generateValueOfType(Class<T> clazz) {
        return generateTrackedValue(clazz, () -> generateWithNext(clazz));
    }

    @Override
    public Object generateValueOfType(Type type) {
        return switch (type) {
            case Class<?> clazz -> generateValueOfType(clazz);
            case WildcardType wildcardType -> generateValueOfType(firstBoundOrObject(wildcardType.getUpperBounds()));
            case TypeVariable<?> typeVariable -> generateValueOfType(firstBoundOrObject(typeVariable.getBounds()));
            case ParameterizedType parameterizedType
                when parameterizedType.getRawType() instanceof Class<?> rawClass ->
                generateTrackedValue(rawClass, () -> generateWithNext(type));
            default -> generateWithNext(type);
        };
    }

    private <T> T generateTrackedValue(Class<?> clazz, Supplier<T> generation) {
        Map<Class<?>, Integer> currentTypes = activeTypes.get();
        if (currentTypes.getOrDefault(clazz, 0) >= MAX_GENERATIONS_PER_TYPE) {
            return null;
        }
        currentTypes.merge(clazz, 1, Integer::sum);
        try {
            return generation.get();
        } finally {
            int count = currentTypes.get(clazz) - 1;
            if (count == 0) {
                currentTypes.remove(clazz);
            } else {
                currentTypes.put(clazz, count);
            }
            if (currentTypes.isEmpty()) {
                activeTypes.remove();
            }
        }
    }
}
