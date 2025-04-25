package com.demo.plugin.exception;

/**
 * @author Erzbir
 * @since 1.0.0
 */
public class PluginIllegalException extends PluginRuntimeException {
    public PluginIllegalException() {
        super();
    }

    public PluginIllegalException(String message) {
        super(message);
    }

    public PluginIllegalException(String message, Throwable cause) {
        super(message, cause);
    }

    public PluginIllegalException(Throwable cause) {
        super(cause);
    }

    public PluginIllegalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
