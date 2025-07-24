package org.example.server.http;

import org.example.annotations.Get;

import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Router {
    private final Map<String, Map.Entry<Object, Method>> getRoutes = new HashMap<>();

    public void registerRoutes(List<Class<?>> controllerClasses) throws Exception {
        for (Class<?> controllerClass : controllerClasses) {
            Object controllerInstance = controllerClass.getDeclaredConstructor().newInstance();
            for (Method method : controllerClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Get.class)) {
                    Get getAnnotation = method.getAnnotation(Get.class);
                    String path = getAnnotation.value();

                    if (getRoutes.containsKey(path)) {
                        throw new IllegalStateException("Duplicate route found for path: " + path);
                    }

                    if (method.getReturnType() != String.class || method.getParameterCount() > 0) {
                        throw new IllegalStateException("Route methods must return String and have no parameters.");
                    }

                    getRoutes.put(path, new AbstractMap.SimpleEntry<>(controllerInstance, method));
                }
            }
        }
    }

    public Map.Entry<Object, Method> getRoute(String path) {
        return getRoutes.get(path);
    }
}
