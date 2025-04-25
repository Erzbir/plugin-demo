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
import com.demo.plugin.internal.loader.PluginLoader;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Erzbir
 * @since 1.0.0
 */
public class JavaPluginManager implements PluginManager {
    private final static String PLUGIN_DIR = "plugins";
    private final Map<String, PluginWrapper> plugins = new HashMap<>();
    private final Map<String, ClassLoader> pluginClassLoaders = new HashMap<>();

    @Override
    public void loadPlugins() {
        loadPlugins(PLUGIN_DIR);
    }

    private void loadPlugins(String pluginDir) {
        File[] plugins = new File(pluginDir).listFiles((dir, name) -> name.endsWith(".jar") || name.endsWith(".class"));
        if (plugins == null || plugins.length == 0) {
            throw new PluginRuntimeException("Plugin directory '" + pluginDir + "' not found");
        }
        for (File plugin : plugins) {
            loadPlugin(plugin.toPath().getFileName());
        }
    }

    public void loadPlugin(Path pluginPath) {
        if (!pluginPath.isAbsolute()) {
            pluginPath = Path.of(PLUGIN_DIR).resolve(pluginPath);
        }

        PluginLoader pluginLoader = new FatJarPluginLoader();

        Plugin plugin = pluginLoader.loadPlugin(pluginPath);

        // 判断这个插件是否继承了 JavaPlugin
        if (!isJavaPlugin(plugin)) {
            throw new PluginIllegalException(String.format("Plugin [%s] Not a JavaPlugin", pluginPath.getFileName()));
        }

        PluginDescription description = ((JavaPlugin) plugin).description;

        // 插件 id 重复
        if (plugins.containsKey(description.id())) {
            throw new PluginAlreadyLoadedException(String.format("Plugin [%s] already loaded with id [%s]", pluginPath.getFileName(), description.id()));
        }

        // 包装成一个 PluginWrapper
        PluginWrapper pluginWrapper = new PluginWrapper(plugin, pluginLoader.getClassLoader(), pluginPath, description);
        // 注册到内部的容器中
        registerPlugin(pluginWrapper, pluginLoader.getClassLoader());
    }

    private void registerPlugin(PluginWrapper plugin, ClassLoader classLoader) {
        String id = plugin.description.id();
        plugins.put(id, plugin);
        // 保存加载这个插件使用的类加载器
        pluginClassLoaders.put(id, classLoader);
        plugin.onLoad();
    }

    private boolean isJavaPlugin(Plugin plugin) {
        return (plugin instanceof JavaPlugin);
    }

    @Override
    public void enablePlugin(String pluginId) {
        PluginWrapper plugin = plugins.get(pluginId);
        if (plugin == null) {
            return;
        }
        if (plugin.isEnable()) {
            return;
        }
        plugin.enable();
        plugin.onEnable();
    }

    @Override
    public void enablePlugins() {
        for (String pluginId : plugins.keySet()) {
            enablePlugin(pluginId);
        }
    }

    @Override
    public void disablePlugin(String pluginId) {
        PluginWrapper plugin = plugins.get(pluginId);
        if (plugin == null) {
            throw new PluginNotFoundException(String.format("Plugin [%s] Not found", pluginId));
        }
        if (!plugin.isEnable()) {
            return;
        }
        plugin.disable();
        plugin.onDisable();
    }

    @Override
    public void disablePlugins() {
        for (String pluginId : plugins.keySet()) {
            disablePlugin(pluginId);
        }
    }

    @Override
    public void unloadPlugins() {
        List<String> keys = new ArrayList<>(plugins.keySet());
        for (String pluginId : keys) {
            unloadPlugin(pluginId);
        }
    }

    @Override
    public void unloadPlugin(String pluginId) {
        PluginWrapper plugin = plugins.remove(pluginId);
        if (plugin == null) {
            throw new PluginNotFoundException(String.format("Plugin [%s] Not found", pluginId));
        }
        plugin.disable();
        plugin.onUnLoad();
        // 移除类加载器
        destroyPlugin(plugin);
        // 帮助 GC 回收这个 plugin, 如果不置空会在未来更远的时间才能被卸载
        plugin = null;
        System.gc();
    }

    private void destroyPlugin(PluginWrapper plugin) {
        try {
            if (pluginClassLoaders.get(plugin.description.id()) instanceof Closeable closeable) {
                closeable.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        pluginClassLoaders.remove(plugin.description.id());
    }
}
