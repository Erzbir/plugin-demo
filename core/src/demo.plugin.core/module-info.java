import com.demo.plugin.PluginManager;

/**
 * @author Erzbir
 * @since 1.0.0
 */
module demo.plugin.core {
    requires demo.plugin.api;
    provides PluginManager with com.demo.plugin.internal.JavaPluginManager;
}