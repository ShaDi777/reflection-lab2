package org.example.generator;

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

    protected <T> T generateWithNext(Class<T> clazz) {
        if (next == null) {
            throw new IllegalArgumentException("Applicable Generator not found");
        }
        return next.generateValueOfType(clazz);
    }

    // TODO think of recursion
    protected <T> T reGenerateValue(Class<T> clazz) {
        return this.root.generateValueOfType(clazz);
    }
}
