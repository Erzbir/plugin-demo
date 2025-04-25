package com.demo.plugin;

/**
 * @author Erzbir
 * @since 1.0.0
 */
public record PluginDescription(String id, String name, String desc, String author, String version) {
    public PluginDescription(String id, String version) {
        this(id, "", "", "", version);
    }

    @Override
    public String toString() {
        return "PluginDescription{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", author='" + author + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
