package it.ax3lt.tla.core;

import dev.dejvokep.boostedyaml.YamlDocument;
import it.ax3lt.tla.TwitchLiveAnnouncerPlugin;
import it.ax3lt.tla.bungee.BungeeService;
import it.ax3lt.tla.database.MysqlService;
import it.ax3lt.tla.metrics.Metrics;
import it.ax3lt.tla.stream.StreamService;

import java.util.Objects;

/**
 * Centralized runtime context for the plugin. Stores commonly accessed singletons
 * such as the plugin instance and loaded configuration documents.
 */
public final class PluginContext {

    private static PluginContext instance;

    private final TwitchLiveAnnouncerPlugin plugin;
    private YamlDocument configuration;
    private YamlDocument messages;
    private Metrics metrics;
    private StreamService streamService;
    private MysqlService mysqlService;
    private BungeeService bungeeService;
    private boolean bungeeMode;

    private PluginContext(TwitchLiveAnnouncerPlugin plugin) {
        this.plugin = plugin;
    }

    public static void initialise(TwitchLiveAnnouncerPlugin plugin) {
        instance = new PluginContext(plugin);
    }

    public static PluginContext get() {
        return Objects.requireNonNull(instance, "Plugin context has not been initialised yet");
    }

    public TwitchLiveAnnouncerPlugin getPlugin() {
        return plugin;
    }

    public YamlDocument getConfiguration() {
        return configuration;
    }

    public void setConfiguration(YamlDocument configuration) {
        this.configuration = configuration;
    }

    public YamlDocument getMessages() {
        return messages;
    }

    public void setMessages(YamlDocument messages) {
        this.messages = messages;
    }

    public Metrics getMetrics() {
        return metrics;
    }

    public void setMetrics(Metrics metrics) {
        this.metrics = metrics;
    }

    public StreamService getStreamService() {
        return streamService;
    }

    public void setStreamService(StreamService streamService) {
        this.streamService = streamService;
    }

    public MysqlService getMysqlService() {
        return mysqlService;
    }

    public void setMysqlService(MysqlService mysqlService) {
        this.mysqlService = mysqlService;
    }

    public BungeeService getBungeeService() {
        return bungeeService;
    }

    public void setBungeeService(BungeeService bungeeService) {
        this.bungeeService = bungeeService;
    }

    public boolean isBungeeMode() {
        return bungeeMode;
    }

    public void setBungeeMode(boolean bungeeMode) {
        this.bungeeMode = bungeeMode;
    }
}
