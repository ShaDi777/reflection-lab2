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
    private static final String CLASS_FILE_SUFFIX = ".class";

    private final List<Class<?>> implementations;

    public GeneratorRegistry(String basePackageName, Class<?>... otherSupportedClasses) {
        Set<Class<?>> implementations = new LinkedHashSet<>(findGeneratableClasses(basePackageName));
        implementations.addAll(List.of(otherSupportedClasses));
        this.implementations = implementations.stream()
                                              .filter(GeneratorRegistry::isImplementable)
                                              .toList();
    }

    public Class<?> getRandomImplementingClass(Class<?> clazz) {
        List<Class<?>> validImplementations = getImplementingClasses(clazz);
        if (validImplementations.isEmpty()) {
            throw new IllegalArgumentException("No implementations in GeneratorRegistry for " + clazz.getTypeName());
        }
        return validImplementations.get(ThreadLocalRandom.current().nextInt(validImplementations.size()));
    }

    public List<Class<?>> getImplementingClasses(Class<?> clazz) {
        if (isImplementable(clazz)) {
            return List.of(clazz);
        }
        return implementations.stream()
                              .filter(clazz::isAssignableFrom)
                              .toList();
    }

    private static boolean isImplementable(Class<?> clazz) {
        return !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers());
    }

    private static List<Class<?>> findGeneratableClasses(String packageName) {
        String packagePath = packageName.replace('.', '/');
        ClassLoader classLoader = GeneratorRegistry.class.getClassLoader();
        Set<String> classNames = findClassNames(packageName, packagePath, classLoader);

        List<Class<?>> candidates = new ArrayList<>();
        for (String className : classNames) {
            if (isMetadataClass(className)) {
                continue;
            }

            Class<?> candidate = loadClass(className, classLoader, packageName);
            if (candidate.isAnnotationPresent(Generatable.class)) {
                candidates.add(candidate);
            }
        }
        return candidates;
    }

    private static Set<String> findClassNames(String packageName, String packagePath, ClassLoader classLoader) {
        Set<String> classNames = new LinkedHashSet<>();
        try {
            Enumeration<URL> resources = classLoader.getResources(packagePath);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                switch (resource.getProtocol()) {
                    case "file" -> scanDirectory(Path.of(resource.toURI()), packagePath, classNames);
                    case "jar" -> scanJar(resource, packagePath, classNames);
                }
            }
        } catch (IOException | URISyntaxException exception) {
            throw new IllegalStateException("Could not scan package " + packageName, exception);
        }
        return classNames;
    }

    private static void scanDirectory(Path packageDirectory, String packagePath, Set<String> classNames)
        throws IOException {
        try (Stream<Path> paths = Files.walk(packageDirectory)) {
            paths.filter(Files::isRegularFile)
                 .filter(path -> path.toString().endsWith(CLASS_FILE_SUFFIX))
                 .map(path -> toClassName(packagePath, packageDirectory.relativize(path)))
                 .forEach(classNames::add);
        }
    }

    private static void scanJar(URL resource, String packagePath, Set<String> classNames) throws IOException {
        JarURLConnection connection = (JarURLConnection) resource.openConnection();
        connection.setUseCaches(false);
        try (JarFile jar = connection.getJarFile()) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                String entryName = entries.nextElement().getName();
                if (entryName.startsWith(packagePath + "/") && entryName.endsWith(CLASS_FILE_SUFFIX)) {
                    classNames.add(entryName.substring(0, entryName.length() - CLASS_FILE_SUFFIX.length())
                                            .replace('/', '.'));
                }
            }
        }
    }

    private static String toClassName(String packagePath, Path relativeClassFile) {
        String relativeClassName = relativeClassFile.toString().replace('\\', '/');
        relativeClassName = relativeClassName.substring(0, relativeClassName.length() - CLASS_FILE_SUFFIX.length());
        return packagePath.replace('/', '.') + "." + relativeClassName.replace('/', '.');
    }

    private static boolean isMetadataClass(String className) {
        return className.endsWith("module-info") || className.endsWith("package-info");
    }

    private static Class<?> loadClass(String className, ClassLoader classLoader, String packageName) {
        try {
            return Class.forName(className, false, classLoader);
        } catch (ClassNotFoundException | LinkageError exception) {
            throw new IllegalStateException(
                "Could not load class " + className + " while scanning package " + packageName,
                exception
            );
        }
    }
}
