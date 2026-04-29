package com.demo.plugin.internal;

import com.demo.plugin.JavaPlugin;
import com.demo.plugin.Plugin;
import com.demo.plugin.PluginDescription;
import com.demo.plugin.PluginManager;
import com.demo.plugin.exception.PluginAlreadyLoadedException;
import com.demo.plugin.exception.PluginIllegalException;
import com.demo.plugin.exception.PluginNotFoundException;
import com.demo.plugin.exception.PluginRuntimeException;
import com.demo.plugin.internal.loader.FatJarPluginLoader;
import com.demo.plugin.internal.loader.PluginLoadResult;
import com.demo.plugin.internal.loader.PluginLoader;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Erzbir
 * @since 1.0.0
 */
public class JavaPluginManager implements PluginManager {
    private static final String PLUGIN_DIR = "plugins";
    private final Map<String, PluginWrapper> plugins = new HashMap<>();

    @Override
    public void loadPlugins() {
        loadPlugins(PLUGIN_DIR);
    }

    private void loadPlugins(String pluginDir) {
        File dir = new File(pluginDir);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new PluginRuntimeException("Plugin directory '" + pluginDir + "' not found");
        }
        File[] pluginFiles = dir.listFiles((d, name) -> name.endsWith(".jar"));
        if (pluginFiles == null) {
            return;
        }
        for (File file : pluginFiles) {
            loadPlugin(file.toPath().getFileName());
        }
    }

    @Override
    public void loadPlugin(Path pluginPath) {
        if (!pluginPath.isAbsolute()) {
            pluginPath = Path.of(PLUGIN_DIR).resolve(pluginPath);
        }

        PluginLoader pluginLoader = new FatJarPluginLoader();
        PluginLoadResult loadResult = pluginLoader.loadPlugin(pluginPath);
        Plugin plugin = loadResult.plugin();
        ClassLoader classLoader = loadResult.classLoader();

        if (!(plugin instanceof JavaPlugin javaPlugin)) {
            throw new PluginIllegalException(
                    String.format("Plugin [%s] is not a JavaPlugin", pluginPath.getFileName()));
        }

        PluginDescription description = javaPlugin.description;
        // 插件 id 重复则抛错
        if (plugins.containsKey(description.id())) {
            throw new PluginAlreadyLoadedException(
                    String.format("Plugin [%s] already loaded with id [%s]",
                            pluginPath.getFileName(), description.id()));
        }

        // 包装成一个 PluginWrapper, 在内部都是操作 PluginWrapper
        PluginWrapper wrapper = new PluginWrapper(plugin, classLoader, pluginPath, description);
        plugins.put(description.id(), wrapper);
        wrapper.onLoad();
    }

    @Override
    public void enablePlugin(String pluginId) {
        PluginWrapper plugin = plugins.get(pluginId);
        if (plugin == null) {
            throw new PluginNotFoundException(
                    String.format("Plugin [%s] not found", pluginId));
        }
        if (plugin.getState() == PluginWrapper.PluginState.ENABLED) {
            return;
        }
        plugin.onEnable();
    }

    @Override
    public void enablePlugins() {
        for (String pluginId : new ArrayList<>(plugins.keySet())) {
            enablePlugin(pluginId);
        }
    }

    @Override
    public void disablePlugin(String pluginId) {
        PluginWrapper plugin = plugins.get(pluginId);
        if (plugin == null) {
            throw new PluginNotFoundException(
                    String.format("Plugin [%s] not found", pluginId));
        }
        if (plugin.getState() != PluginWrapper.PluginState.ENABLED) {
            return;
        }
        plugin.onDisable();
    }

    @Override
    public void disablePlugins() {
        for (String pluginId : new ArrayList<>(plugins.keySet())) {
            disablePlugin(pluginId);
        }
    }

    @Override
    public void unloadPlugin(String pluginId) {
        PluginWrapper plugin = plugins.remove(pluginId);
        if (plugin == null) {
            throw new PluginNotFoundException(
                    String.format("Plugin [%s] not found", pluginId));
        }
        if (plugin.getState() == PluginWrapper.PluginState.ENABLED) {
            plugin.onDisable();
        }
        plugin.onUnLoad();
        // 移除类加载器
        destroyClassLoader(plugin);
        // 方法中存在 plugin 引用, 需要在 gc 之前断开引用
        plugin = null;
        // 存在副作用, 但通知 gc 可以一定程度上加快卸载
        System.gc();
    }

    @Override
    public void unloadPlugins() {
        for (String pluginId : new ArrayList<>(plugins.keySet())) {
            unloadPlugin(pluginId);
        }
    }

    private void destroyClassLoader(PluginWrapper plugin) {
        if (plugin.classLoader instanceof Closeable closeable) {
            try {
                closeable.close();
            } catch (IOException e) {
                throw new PluginRuntimeException(
                        String.format("Plugin [%s] classLoader close failed", plugin.description.id()), e);
            }
        }
    }
}