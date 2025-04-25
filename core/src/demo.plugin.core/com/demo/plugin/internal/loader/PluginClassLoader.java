package com.demo.plugin.internal.loader;

import com.demo.plugin.exception.PluginRuntimeException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author Erzbir
 * @since 1.0.0
 */
public class PluginClassLoader extends URLClassLoader {
    private static final String JAVA_PACKAGE_PREFIX = "java.";
    private static final String JAVAX_PACKAGE_PREFIX = "javax.";
    private static final String PLUGIN_PACKAGE_PREFIX = "com.demo.plugin.";

    public PluginClassLoader() {
        super(new URL[0]);
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }

    public void addFile(File file) {
        try {
            addURL(file.getCanonicalFile().toURI().toURL());
        } catch (IOException e) {
            throw new PluginRuntimeException(e);
        }
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(className)) {
            // 如果是 Java 自带的系统类就交给 SystemClassLoader 来加载
            if (className.startsWith(JAVA_PACKAGE_PREFIX) || className.startsWith(JAVAX_PACKAGE_PREFIX)) {
                return findSystemClass(className);
            }

            // 如果是插件框架的依赖, 交给父类加载器
            if (className.startsWith(PLUGIN_PACKAGE_PREFIX)) {
                ClassLoader parent = getParent();
                if (parent != null) {
                    return parent.loadClass(className);
                }
            }

            // 先查找有没有这个类, 加载过的类就直接返回
            Class<?> loadedClass = findLoadedClass(className);
            if (loadedClass != null) {
                return loadedClass;
            }

            try {
                Class<?> c = findClass(className);

                if (c != null) {
                    return c;
                }
            } catch (ClassNotFoundException ignored) {

            }
        }

        throw new ClassNotFoundException(className);
    }
}
