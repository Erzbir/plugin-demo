package com.demo.plugin;

/**
 * @author Erzbir
 * @since 1.0.0
 */
public abstract class JavaPlugin implements Plugin {
    public final PluginDescription description;

    public JavaPlugin(final PluginDescription description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "JavaPlugin{" +
                "description=" + description +
                '}';
    }
}
