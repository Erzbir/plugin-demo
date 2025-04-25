package com.demo.plugin.exception;

import com.demo.plugin.exception.PluginRuntimeException;

/**
 * @author Erzbir
 * @since 1.0.0
 */
public class PluginAlreadyLoadedException extends PluginRuntimeException {
    public PluginAlreadyLoadedException() {
        super();
    }

    public PluginAlreadyLoadedException(String message) {
        super(message);
    }

    public PluginAlreadyLoadedException(String message, Throwable cause) {
        super(message, cause);
    }

    public PluginAlreadyLoadedException(Throwable cause) {
        super(cause);
    }

    public PluginAlreadyLoadedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
