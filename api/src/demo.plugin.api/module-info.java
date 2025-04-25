import com.demo.plugin.PluginManager;

/**
 * @author Erzbir
 * @since 1.0.0
 */
module demo.plugin.api {
    uses PluginManager;
    exports com.demo.plugin;
    exports com.demo.plugin.exception;
}