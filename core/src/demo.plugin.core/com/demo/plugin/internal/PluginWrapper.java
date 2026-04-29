package com.demo.plugin.internal;

import com.demo.plugin.Plugin;
import com.demo.plugin.PluginDescription;
import com.demo.plugin.internal.exception.PluginLifecycleException;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Erzbir
 * @since 1.0.0
 */
class PluginWrapper implements Plugin {
    public final PluginDescription description;
    public final ClassLoader classLoader;
    public final Path pluginPath;
    private final Plugin delegate;
    private final AtomicReference<PluginState> state = new AtomicReference<>(PluginState.CREATED);

    enum PluginState {
        CREATED,
        LOADED,
        ENABLED,
        DISABLED,
        UNLOADED
    }

    public PluginWrapper(Plugin plugin, ClassLoader classLoader, Path pluginPath, PluginDescription description) {
        this.delegate = plugin;
        this.classLoader = classLoader;
        this.pluginPath = pluginPath;
        this.description = description;
    }

    @Override
    public void onLoad() {
        if (state.get() != PluginState.CREATED) {
            throw new IllegalStateException(
                    String.format("[%s] onLoad() called in invalid state: %s", description.name(), state.get())
            );
        }
        try {
            delegate.onLoad();
        } catch (Exception e) {
            throw new PluginLifecycleException(description.name(), "onLoad", e);
        }
        state.set(PluginState.LOADED);
    }

    @Override
    public void onEnable() {
        PluginState current = state.get();
        if (current != PluginState.LOADED && current != PluginState.DISABLED) {
            throw new IllegalStateException(
                    String.format("[%s] onEnable() called in invalid state: %s", description.name(), current)
            );
        }
        try {
            delegate.onEnable();
        } catch (Exception e) {
            throw new PluginLifecycleException(description.name(), "onEnable", e);
        }
        state.set(PluginState.ENABLED);
    }

    @Override
    public void onDisable() {
        if (state.get() != PluginState.ENABLED) {
            throw new IllegalStateException(
                    String.format("[%s] onDisable() called in invalid state: %s", description.name(), state.get())
            );
        }
        try {
            delegate.onDisable();
        } catch (Exception e) {
            throw new PluginLifecycleException(description.name(), "onDisable", e);
        }
        state.set(PluginState.DISABLED);
    }

    @Override
    public void onUnLoad() {
        PluginState current = state.get();
        if (current != PluginState.DISABLED && current != PluginState.LOADED) {
            throw new IllegalStateException(
                    String.format("[%s] onUnLoad() called in invalid state: %s", description.name(), current)
            );
        }
        try {
            delegate.onUnLoad();
        } catch (Exception e) {
            throw new PluginLifecycleException(description.name(), "onUnLoad", e);
        }
        state.set(PluginState.UNLOADED);
    }

    public PluginState getState() {
        return state.get();
    }
}