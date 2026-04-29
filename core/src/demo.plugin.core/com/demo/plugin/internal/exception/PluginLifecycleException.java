package com.demo.plugin.internal.exception;

/**
 * @author Erzbir
 * @since 1.0.0
 */
public class PluginLifecycleException extends RuntimeException {
    public PluginLifecycleException(String pluginName, String phase, Throwable cause) {
        super(String.format("Plugin [%s] threw an exception during %s()", pluginName, phase), cause);
    }
}