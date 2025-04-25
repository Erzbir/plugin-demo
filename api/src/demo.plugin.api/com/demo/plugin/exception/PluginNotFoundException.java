package com.demo.plugin.exception;

public class PluginNotFoundException extends PluginRuntimeException {
    public PluginNotFoundException() {
        super();
    }

    public PluginNotFoundException(String message) {
        super(message);
    }

    public PluginNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PluginNotFoundException(Throwable cause) {
        super(cause);
    }

    public PluginNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public PluginNotFoundException(String message, Object... args) {
        super(String.format(message, args));
    }
}
