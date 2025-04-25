package com.demo.plugin;


import java.util.*;

/**
 * @author Erzbir
 * @since 1.0.0
 */
public class PluginManagerProvider {
    public static final PluginManagerProvider INSTANCE = new PluginManagerProvider();
    private static final Map<String, PluginManager> PLUGIN_MANAGERS = new HashMap<>();

    static {
        ServiceLoader<PluginManager> serviceLoader = ServiceLoader.load(PluginManager.class);
        for (PluginManager pluginManager : serviceLoader) {
            PLUGIN_MANAGERS.put(pluginManager.getClass().getName(), pluginManager);
        }
    }

    public PluginManager getInstance(String key) {
        return PLUGIN_MANAGERS.get(key);
    }

    public PluginManager getInstance() {
        for (PluginManager pluginManager : PLUGIN_MANAGERS.values()) {
            return pluginManager;
        }
        throw new IllegalStateException("No PluginManagers available");
    }

    public List<PluginManager> getInstances() {
        return new ArrayList<>(PLUGIN_MANAGERS.values());
    }
}
