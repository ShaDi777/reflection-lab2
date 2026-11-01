package org.example.generator;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.concurrent.ThreadLocalRandom;

public abstract class BaseGenerator implements Generator {
    protected static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    protected Generator root = this;
    protected Generator next = null;

    @Override
    public void setRoot(Generator root) {
        this.root = root;
    }

    @Override
    public Generator getRoot() {
        return this.root;
    }

    @Override
    public Generator setNext(Generator next) {
        Generator cur = next;
        while (cur != null) {
            cur.setRoot(this.root);
            cur = cur.getNext();
        }

        this.next = next;
        return next;
    }

    @Override
    public Generator getNext() {
        return next;
    }

    @Override
    public Object generateValueOfType(Type type) {
        return switch (type) {
            case Class<?> clazz -> generateValueOfType(clazz);
            case WildcardType wildcardType -> generateValueOfType(firstBoundOrObject(wildcardType.getUpperBounds()));
            case TypeVariable<?> typeVariable -> generateValueOfType(firstBoundOrObject(typeVariable.getBounds()));
            default -> generateWithNext(type);
        };
    }

    protected static Type firstBoundOrObject(Type[] bounds) {
        return bounds.length == 0 ? Object.class : bounds[0];
    }

    /** CHAIN FUNCTIONS */
    protected <T> T generateWithNext(Class<T> clazz) {
        if (next == null) {
            throw new IllegalArgumentException("Applicable generator not found for " + clazz.getTypeName());
        }
        return next.generateValueOfType(clazz);
    }

    protected Object generateWithNext(Type type) {
        if (next == null) {
            throw new IllegalArgumentException("Applicable generator not found for " + type.getTypeName());
        }
        return next.generateValueOfType(type);
    }

    /** RE-GENERATE FROM START FUNCTIONS */
    protected <T> T reGenerateValue(Class<T> clazz) {
        return root.generateValueOfType(clazz);
    }

    protected Object reGenerateValue(Type type) {
        return root.generateValueOfType(type);
    }
}
