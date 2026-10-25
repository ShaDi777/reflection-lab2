package org.example.generator;

public interface Generator {
    void setRoot(Generator root);

    Generator getRoot();

    Generator setNext(Generator next);

    Generator getNext();

    <T> T generateValueOfType(Class<T> clazz);
    //throws InvocationTargetException, InstantiationException, IllegalAccessException
}
