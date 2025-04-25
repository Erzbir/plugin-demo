package com.demo.plugin.internal.loader;

import com.demo.plugin.Plugin;

import java.nio.file.Path;

/**
 * @author Erzbir
 * @since 1.0.0
 */
public interface PluginLoader {
    Plugin loadPlugin(Path pluginPath);

    ClassLoader getClassLoader();
}
