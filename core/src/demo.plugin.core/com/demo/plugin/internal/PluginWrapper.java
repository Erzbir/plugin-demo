package com.demo.plugin.internal;

import com.demo.plugin.Plugin;
import com.demo.plugin.PluginDescription;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Erzbir
 * @since 1.0.0
 */
public class PluginWrapper implements Plugin {
    public final PluginDescription description;
    public final ClassLoader pluginContext;
    public final Path pluginPath;
    private final Plugin delegate;
    private final AtomicBoolean enable = new AtomicBoolean(false);

    public PluginWrapper(Plugin plugin, ClassLoader classLoader, Path pluginPath, PluginDescription description) {
        this.delegate = plugin;
        this.pluginContext = classLoader;
        this.pluginPath = pluginPath;
        this.description = description;
    }

    @Override
    public void onEnable() {
        delegate.onEnable();
    }

    @Override
    public void onDisable() {
        delegate.onDisable();
    }

    @Override
    public void onLoad() {
        delegate.onLoad();
    }

    @Override
    public void onUnLoad() {
        delegate.onUnLoad();
    }

    public boolean isEnable() {
        return enable.get();
    }

    public void enable() {
        enable.set(true);
    }

    public void disable() {
        enable.set(false);
    }
}
