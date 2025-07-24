package org.example.server;

import org.example.annotations.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassPathScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassPathScanner.class);

    public static List<Class<?>> findControllerClasses(String basePackage) throws IOException, ClassNotFoundException {
        List<Class<?>> controllerClasses = new ArrayList<>();
        String path = basePackage.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(path);

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (resource.getProtocol().equals("jar")) {
                String jarPath = resource.getPath().substring(5, resource.getPath().indexOf("!"));
                try (JarFile jarFile = new JarFile(jarPath)) {
                    Enumeration<JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String entryName = entry.getName();
                        if (entryName.startsWith(path) && entryName.endsWith(".class")) {
                            String className = entryName.replace('/', '.').substring(0, entryName.length() - 6);
                            try {
                                Class<?> clazz = Class.forName(className);
                                if (clazz.isAnnotationPresent(Controller.class)) {
                                    controllerClasses.add(clazz);
                                }
                            } catch (ClassNotFoundException | NoClassDefFoundError e) {
                                LOGGER.error("Could not load class: {}", className, e);
                            }
                        }
                    }
                }
            } else {
                File directory = new File(resource.getFile());
                scanClassesInDirectory(directory, basePackage, controllerClasses);
            }
        }
        return controllerClasses;
    }

    private static void scanClassesInDirectory(File directory, String packageName, List<Class<?>> classes) {
        if (!directory.exists()) {
            return;
        }
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                scanClassesInDirectory(file, packageName + "." + file.getName(), classes);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                try {
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(Controller.class)) {
                        classes.add(clazz);
                    }
                } catch (ClassNotFoundException | NoClassDefFoundError e) {
                    LOGGER.error("Could not load class: {}", className, e);
                }
            }
        }
    }
}
