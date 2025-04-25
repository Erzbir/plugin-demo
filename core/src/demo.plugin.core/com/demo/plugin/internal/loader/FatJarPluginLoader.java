package com.demo.plugin.internal.loader;


import com.demo.plugin.Plugin;
import com.demo.plugin.exception.PluginIllegalException;
import com.demo.plugin.internal.util.FileTypeDetector;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.jar.JarFile;

/**
 * @author Erzbir
 * @since 1.0.0
 */
public class FatJarPluginLoader implements PluginLoader {
    protected final PluginClassLoader classLoader;
    protected final static String SERVICE_PATH = "META-INF/services/com.demo.plugin.Plugin";

    public FatJarPluginLoader() {
        this.classLoader = new PluginClassLoader();
    }

    @Override
    public Plugin loadPlugin(Path pluginPath) {
        // 判断是否是一个 jar 文件, 通过文件头和后缀判断
        if (!FileTypeDetector.isJarFile(pluginPath)) {
            throw new PluginIllegalException("The file " + pluginPath.toAbsolutePath() + " is not a jar file");
        }
        File file = pluginPath.toFile();
        // 加载这个插件的所有字节码文件
        loadPluginClass(file);
        // 读取插件的主类名
        String pluginClassName = readPluginMainClassName(file);
        // 获取插件的主类, 在这个方法里真正开始加载类
        Class<?> pluginClass = getPluginClass(pluginClassName);
        // 返回插件实例
        return resolvePluginClass(pluginClass);
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    private void loadPluginClass(File file) {
        classLoader.addFile(file);
    }

    private Class<?> getPluginClass(String className) {
        try {
            return Class.forName(className, false, classLoader);
        } catch (ClassNotFoundException e) {
            throw new PluginIllegalException(e);
        }
    }

    private Plugin resolvePluginClass(Class<?> pluginClass) {
        if (Plugin.class.isAssignableFrom(pluginClass)) {
            try {
                // 由于使用了模块化, 并且动态加载的 jar 是一个 unnamed module 所以不能使用 MethodHandle
                Field instance = pluginClass.getDeclaredField("INSTANCE");
                instance.setAccessible(true);
                return (Plugin) instance.get(null);
            } catch (Throwable e) {
                throw new PluginIllegalException(String.format("Failed to find INSTANCE in plugin: %s", pluginClass.getName()), e);
            }
        }
        throw new PluginIllegalException("Should never get here");
    }

    private String readPluginMainClassName(File file) {
        String line;
        try (JarFile jarFile = new JarFile(file); BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(jarFile.getInputStream(jarFile.getEntry(SERVICE_PATH))))) {
            line = bufferedReader.readLine();
        } catch (IOException e) {
            throw new PluginIllegalException("Failed to read service file", e);
        }
        return line;
    }
}
