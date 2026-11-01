package org.example.classes;

import java.util.List;
import java.util.Map;

public class Complex<T> {
    private final Map<String, List<? extends T[]>> field;

    public Complex(Map<String, List<? extends T[]>> field) {
        this.field = field;
    }

    public Map<String, List<? extends T[]>> getField() {
        return field;
    }
}
