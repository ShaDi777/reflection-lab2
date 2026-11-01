package org.example.generator;

public class StringGenerator extends BaseGenerator {
    private static final char[] CHARACTERS =
        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
    private static final int MIN_LENGTH = 5;
    private static final int MAX_LENGTH = 12;

    @Override
    public <T> T generateValueOfType(Class<T> clazz) {
        if (clazz != String.class) {
            return generateWithNext(clazz);
        }

        int length = RANDOM.nextInt(MIN_LENGTH, MAX_LENGTH + 1);
        StringBuilder value = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            value.append(CHARACTERS[RANDOM.nextInt(CHARACTERS.length)]);
        }
        return clazz.cast(value.toString());
    }
}
