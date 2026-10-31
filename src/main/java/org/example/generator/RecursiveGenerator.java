package org.example.generator;

import java.util.HashMap;
import java.util.Map;

public final class RecursiveGenerator extends BaseGenerator {
    private static final int MAX_GENERATIONS_PER_TYPE = 2;

    private final ThreadLocal<Map<Class<?>, Integer>> activeTypes =
        ThreadLocal.withInitial(HashMap::new);

    @Override
    public <T> T generateValueOfType(Class<T> clazz) {
        Map<Class<?>, Integer> currentTypes = activeTypes.get();
        if (currentTypes.getOrDefault(clazz, 0) >= MAX_GENERATIONS_PER_TYPE) {
            return null;
        }
        currentTypes.merge(clazz, 1, Integer::sum);
        try {
            return generateWithNext(clazz);
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
