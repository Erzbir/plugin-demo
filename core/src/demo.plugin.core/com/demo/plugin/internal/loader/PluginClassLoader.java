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
class PluginClassLoader extends URLClassLoader {
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
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            // 先查找是否被此类加载器加载过
            Class<?> loaded = findLoadedClass(name);
            if (loaded != null) {
                if (resolve) {
                    resolveClass(loaded);
                }
                return loaded;
            }

            // 如果 Java 自带的系统类或是插件框架的依赖, 交给父类加载器
            if (name.startsWith(JAVA_PACKAGE_PREFIX)
                    || name.startsWith(JAVAX_PACKAGE_PREFIX)
                    || name.startsWith(PLUGIN_PACKAGE_PREFIX)) {
                ClassLoader parent = getParent();
                if (parent == null) {
                    throw new ClassNotFoundException(name);
                }
                return parent.loadClass(name);
            }

            // 插件自身类 -> 本 ClassLoader
            Class<?> c = findClass(name);
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }
}
