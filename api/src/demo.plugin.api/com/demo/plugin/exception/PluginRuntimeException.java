package com.demo.plugin.exception;

/**
 * @author Erzbir
 * @since 1.0.0
 */
public class PluginRuntimeException extends RuntimeException {
    public PluginRuntimeException() {
        super();
    }

    public PluginRuntimeException(String message) {
        super(message);
    }

    public PluginRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public PluginRuntimeException(Throwable cause) {
        super(cause);
    }

    public PluginRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public PluginRuntimeException(String message, Object... args) {
        super(String.format(message, args));
    }
}
