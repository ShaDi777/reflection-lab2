package org.example.generator;

import java.lang.reflect.Type;

public interface Generator {
    void setRoot(Generator root);

    Generator getRoot();

    Generator setNext(Generator next);

    Generator getNext();

    <T> T generateValueOfType(Class<T> clazz);

    Object generateValueOfType(Type type);
    //throws InvocationTargetException, InstantiationException, IllegalAccessException
}
