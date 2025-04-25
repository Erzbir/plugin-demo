package com.demo.plugin;

/**
 * @author Erzbir
 * @since 1.0.0
 */
public interface Plugin {
    void onEnable();

    void onDisable();

    void onLoad();

    void onUnLoad();
}
