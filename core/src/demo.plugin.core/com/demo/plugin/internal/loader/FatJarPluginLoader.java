package com.demo.plugin.internal.loader;


import com.demo.plugin.Plugin;
import com.demo.plugin.exception.PluginIllegalException;
import com.demo.plugin.internal.util.FileTypeDetector;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Erzbir
 * @since 1.0.0
 */
public class FatJarPluginLoader implements PluginLoader {
    protected static final String SERVICE_PATH = "META-INF/services/com.demo.plugin.Plugin";

    @Override
    public PluginLoadResult loadPlugin(Path pluginPath) {
        if (!FileTypeDetector.isJarFile(pluginPath)) {
            throw new PluginIllegalException(
                    "The file " + pluginPath.toAbsolutePath() + " is not a jar file");
        }

        File file = pluginPath.toFile();

        // 每个插件独享一个 ClassLoader, 保证隔离和可卸载
        PluginClassLoader classLoader = new PluginClassLoader();
        // 加载这个插件的所有字节码文件, 会在 getPluginClass 的时候加载
        classLoader.addFile(file);

        String pluginClassName = readPluginMainClassName(file);
        Class<?> pluginClass = getPluginClass(pluginClassName, classLoader);
        Plugin plugin = resolvePluginClass(pluginClass);
        return new PluginLoadResult(plugin, classLoader);
    }

    private Class<?> getPluginClass(String className, PluginClassLoader classLoader) {
        try {
            // initialize=true: 确保静态初始化块执行, INSTANCE 等字段正常赋值
            return Class.forName(className, true, classLoader);
        } catch (ClassNotFoundException e) {
            throw new PluginIllegalException("Plugin class not found: " + className, e);
        }
    }

    private Plugin resolvePluginClass(Class<?> pluginClass) {
        if (!Plugin.class.isAssignableFrom(pluginClass)) {
            throw new PluginIllegalException(
                    pluginClass.getName() + " does not implement Plugin");
        }
        try {
            return revolvePlugin(pluginClass);
        } catch (NoSuchFieldException e) {
            throw new PluginIllegalException(
                    "Plugin " + pluginClass.getName() + " must declare a static INSTANCE field", e);
        } catch (IllegalAccessException e) {
            throw new PluginIllegalException(
                    "Cannot access INSTANCE field in " + pluginClass.getName(), e);
        }
    }

    private Plugin revolvePlugin(Class<?> pluginClass) throws NoSuchFieldException, IllegalAccessException {
        Field instance = pluginClass.getDeclaredField("INSTANCE");

        if (!Modifier.isStatic(instance.getModifiers())) {
            throw new PluginIllegalException(
                    "INSTANCE field in " + pluginClass.getName() + " must be static");
        }

        instance.setAccessible(true);

        Object value = instance.get(null);
        if (value == null) {
            throw new PluginIllegalException(
                    "INSTANCE field in " + pluginClass.getName() + " is null");
        }
        if (value instanceof Plugin plugin) {
            return plugin;
        }
        throw new PluginIllegalException(
                "INSTANCE field in " + pluginClass.getName()
                        + " is not assignable to Plugin, actual type: " + value.getClass().getName());
    }

    private String readPluginMainClassName(File file) {
        try (JarFile jarFile = new JarFile(file)) {
            JarEntry entry = jarFile.getJarEntry(SERVICE_PATH);
            if (entry == null) {
                throw new PluginIllegalException(
                        "Missing service file in plugin jar: " + SERVICE_PATH);
            }
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(jarFile.getInputStream(entry)))) {
                String line = reader.readLine();
                if (line == null || line.isBlank()) {
                    throw new PluginIllegalException(
                            "Service file is empty: " + SERVICE_PATH);
                }
                return line.trim();
            }
        } catch (IOException e) {
            throw new PluginIllegalException("Failed to read service file: " + SERVICE_PATH, e);
        }
    }
}