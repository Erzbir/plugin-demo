package com.demo.plugin.internal.loader;

import java.nio.file.Path;

/**
 * @author Erzbir
 * @since 1.0.0
 */
public interface PluginLoader {
    PluginLoadResult loadPlugin(Path pluginPath);
}
