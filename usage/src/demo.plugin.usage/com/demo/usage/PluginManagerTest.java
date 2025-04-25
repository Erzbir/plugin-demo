package com.demo.usage;

import com.demo.plugin.PluginManager;
import com.demo.plugin.PluginManagerProvider;

/**
 * @author Erzbir
 * @since 1.0.0
 */
public class PluginManagerTest {
    public static void main(String[] args) {
        PluginManager pluginManager = PluginManagerProvider.INSTANCE.getInstance();
        pluginManager.loadPlugins();
        pluginManager.enablePlugins();
        pluginManager.unloadPlugins();
    }
}
