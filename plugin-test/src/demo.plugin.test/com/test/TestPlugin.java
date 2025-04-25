package com.test;

import com.demo.plugin.JavaPlugin;
import com.demo.plugin.PluginDescription;

/**
 * @author Erzbir
 * @since 1.0.0
 */
public class TestPlugin extends JavaPlugin {
    public final static TestPlugin INSTANCE = new TestPlugin();

    public TestPlugin() {
        super(new PluginDescription("test", "TestPlugin", "Just a test", "Erzbir", "0.0.1"));
    }

    @Override
    public void onEnable() {
        System.out.printf("Plugin %s enabled\n", description.id());
    }

    @Override
    public void onDisable() {
        System.out.printf("Plugin %s disable\n", description.id());
    }

    @Override
    public void onLoad() {
        System.out.printf("Plugin %s loaded\n", description.id());
    }

    @Override
    public void onUnLoad() {
        System.out.printf("Plugin %s unloaded\n", description.id());
    }
}