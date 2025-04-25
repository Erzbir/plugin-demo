package com.demo.plugin;

import java.nio.file.Path;

/**
 * @author Erzbir
 * @since 1.0.0
 */
public interface PluginManager {
    void loadPlugins();

    void loadPlugin(Path pluginPath);

    void enablePlugin(String pluginId);

    void enablePlugins();

    void disablePlugin(String pluginId);

    void disablePlugins();

    void unloadPlugins();

    void unloadPlugin(String pluginId);
}
