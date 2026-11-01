package org.example.generator;

public class EnumGenerator extends BaseGenerator {

    @Override
    public <T> T generateValueOfType(Class<T> clazz) {
        // Enum class
        if (clazz.isEnum()) {
            var values = clazz.getEnumConstants();
            if (values == null || values.length == 0) {
                throw new IllegalArgumentException("Enum does not have values: " + clazz.getTypeName());
            }
            return values[RANDOM.nextInt(values.length)];
        }

        return generateWithNext(clazz);
    }
}
