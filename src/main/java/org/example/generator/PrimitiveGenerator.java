package org.example.generator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PrimitiveGenerator extends BaseGenerator {
    private static final Map<Class<?>, Supplier<?>> GENERATORS = new HashMap<>();

    static {
        addPairedGenerator(byte.class, Byte.class, () -> (byte) RANDOM.nextInt(127));
        addPairedGenerator(short.class, Short.class, () -> (short) RANDOM.nextInt(1024));
        addPairedGenerator(int.class, Integer.class, () -> RANDOM.nextInt(2048));
        addPairedGenerator(long.class, Long.class, () -> RANDOM.nextLong(4096));
        addPairedGenerator(float.class, Float.class, () -> RANDOM.nextFloat(1024));
        addPairedGenerator(double.class, Double.class, () -> RANDOM.nextDouble(2048));
        addPairedGenerator(char.class, Character.class, () -> (char) (RANDOM.nextInt(26) + 'a'));
        addPairedGenerator(boolean.class, Boolean.class, RANDOM::nextBoolean);
        addPairedGenerator(void.class, Void.class, () -> null);
    }

    private static <T> void addPairedGenerator(Class<T> primitive, Class<T> wrapper, Supplier<T> supplier) {
        GENERATORS.put(primitive, supplier);
        GENERATORS.put(wrapper, supplier);
    }

    @Override
    public <T> T generateValueOfType(Class<T> clazz) {
        if (clazz.isPrimitive() || GENERATORS.containsKey(clazz)) {
            return (T) GENERATORS.get(clazz).get();
        }

        return generateWithNext(clazz);
    }
}
