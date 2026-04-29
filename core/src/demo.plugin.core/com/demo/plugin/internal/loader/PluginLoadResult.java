package com.demo.plugin.internal.loader;

import com.demo.plugin.Plugin;

public record PluginLoadResult(Plugin plugin, ClassLoader classLoader) {
}