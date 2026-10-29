package org.example.generator.registry;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class GeneratorRegistry {
    private final List<Class<?>> implementations;

    public GeneratorRegistry(String basePackageName, Class<?>... otherSupportedClasses) {
        Set<Class<?>> implementations = new LinkedHashSet<>(findGeneratableClasses(basePackageName));
        implementations.addAll(List.of(otherSupportedClasses));
        this.implementations = implementations.stream().filter(GeneratorRegistry::isImplementable).toList();
    }

    public Class<?> getRandomImplementingClass(Class<?> clazz) {
        var validImplementations = getImplementingClasses(clazz);
        if (validImplementations.isEmpty()) {
            throw new IllegalArgumentException("No implementations in GeneratorRegistry for " + clazz.getTypeName());
        }
        return validImplementations.get(ThreadLocalRandom.current().nextInt(validImplementations.size()));
    }

    public List<Class<?>> getImplementingClasses(Class<?> clazz) {
        if (isImplementable(clazz)) {
            return List.of(clazz);
        }

        return implementations.stream().filter(clazz::isAssignableFrom).toList();
    }

    private static boolean isImplementable(Class<?> clazz) {
        return !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers());
    }

    private static List<Class<?>> findGeneratableClasses(String basePackageName) {
        String packagePath = basePackageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = GeneratorRegistry.class.getClassLoader();
        }

        Set<String> classNames = new LinkedHashSet<>();
        try {
            Enumeration<URL> resources = classLoader.getResources(packagePath);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                if ("file".equals(resource.getProtocol())) {
                    Path packageDirectory = Path.of(resource.toURI());
                    try (Stream<Path> paths = Files.walk(packageDirectory)) {
                        paths.filter(Files::isRegularFile)
                             .filter(path -> path.toString().endsWith(".class"))
                             .forEach(path -> classNames.add(toClassName(packagePath, packageDirectory, path)));
                    }
                } else if ("jar".equals(resource.getProtocol())) {
                    JarURLConnection connection = (JarURLConnection) resource.openConnection();
                    connection.setUseCaches(false);
                    try (JarFile jar = connection.getJarFile()) {
                        Enumeration<JarEntry> entries = jar.entries();
                        while (entries.hasMoreElements()) {
                            String entryName = entries.nextElement().getName();
                            if (entryName.startsWith(packagePath + "/") && entryName.endsWith(".class")) {
                                classNames.add(entryName.substring(0, entryName.length() - 6)
                                                        .replace('/', '.'));
                            }
                        }
                    }
                }
            }
        } catch (IOException | URISyntaxException exception) {
            throw new IllegalStateException("Could not scan package " + basePackageName, exception);
        }

        List<Class<?>> generatableClasses = new ArrayList<>();
        for (String className : classNames) {
            if (className.endsWith("module-info") || className.endsWith("package-info")) {
                continue;
            }
            try {
                Class<?> candidate = Class.forName(className, false, classLoader);
                if (candidate.isAnnotationPresent(Generatable.class)) {
                    generatableClasses.add(candidate);
                }
            } catch (ClassNotFoundException | LinkageError exception) {
                throw new IllegalStateException("Could not load class " + className, exception);
            }
        }
        return generatableClasses;
    }

    private static String toClassName(String packagePath, Path packageDirectory, Path classFile) {
        String relativeClassName = packageDirectory.relativize(classFile).toString();
        return packagePath.replace('/', '.') + "."
                   + relativeClassName.substring(0, relativeClassName.length() - 6)
                                      .replace('\\', '/')
                                      .replace('/', '.');
    }
}
