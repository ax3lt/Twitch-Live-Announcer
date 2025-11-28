package it.ax3lt.tla;

import dev.dejvokep.boostedyaml.YamlDocument;
import it.ax3lt.tla.bungee.BungeeService;
import it.ax3lt.tla.command.CommandRegistrar;
import it.ax3lt.tla.config.ConfigurationManager;
import it.ax3lt.tla.core.PluginContext;
import it.ax3lt.tla.database.MysqlService;
import it.ax3lt.tla.listener.StreamerJoinListener;
import it.ax3lt.tla.metrics.Metrics;
import it.ax3lt.tla.stream.StreamScheduler;
import it.ax3lt.tla.stream.StreamService;
import it.ax3lt.tla.tabcomplete.StreamCommandTabCompleter;
import it.ax3lt.tla.update.PluginUpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public final class TwitchLiveAnnouncerPlugin extends JavaPlugin {

    private ConfigurationManager configurationManager;
    private StreamService streamService;
    private StreamScheduler streamScheduler;
    private MysqlService mysqlService;
    private BungeeService bungeeService;
    private CommandRegistrar commandRegistrar;
    private PluginUpdateChecker updateChecker;

    @Override
    public void onEnable() {
        PluginContext.initialise(this);

        configurationManager = new ConfigurationManager(this);
        commandRegistrar = new CommandRegistrar(this);
        bungeeService = new BungeeService(this);
        PluginContext.get().setBungeeService(bungeeService);
        updateChecker = new PluginUpdateChecker(this);

        loadConfiguration();
        configurationManager.scheduleBackups();

        if (!initialiseStreamService()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (!initialiseDatabase()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        registerPlaceholderExpansion();
        registerMetrics();
        updateChecker.checkForUpdates();
        bungeeService.register();
        commandRegistrar.registerCommands();
        commandRegistrar.registerTabCompleter(new StreamCommandTabCompleter());
        Bukkit.getPluginManager().registerEvents(new StreamerJoinListener(), this);

        streamScheduler = new StreamScheduler(this, streamService);
        streamScheduler.start();
    }

    @Override
    public void onDisable() {
        bungeeService.unregister();
        if (PluginContext.get().getMetrics() != null) {
            PluginContext.get().getMetrics().shutdown();
        }
    }

    private void loadConfiguration() {
        try {
            YamlDocument configuration = configurationManager.loadConfig();
            YamlDocument messages = configurationManager.loadMessages();
            PluginContext.get().setConfiguration(configuration);
            PluginContext.get().setMessages(messages);
            saveDefaultConfig();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load configuration files", exception);
        }
    }

    private boolean initialiseStreamService() {
        streamService = new StreamService(this);
        PluginContext.get().setStreamService(streamService);
        try {
            streamService.configureParameters();
            return true;
        } catch (IOException exception) {
            getServer().getConsoleSender().sendMessage(configurationManager.createParametersErrorMessage(exception.getMessage()));
            return false;
        }
    }

    private boolean initialiseDatabase() {
        mysqlService = new MysqlService(this);
        PluginContext.get().setMysqlService(mysqlService);
        if (!mysqlService.isEnabled()) {
            return true;
        }

        mysqlService.loadSettings();
        if (!mysqlService.canConnect()) {
            return false;
        }

        mysqlService.setupTable();
        mysqlService.clearTable();
        return true;
    }

    private void registerPlaceholderExpansion() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            updateChecker.registerPlaceholderExpansion();
        }
    }

    private void registerMetrics() {
        if (!PluginContext.get().getConfiguration().getBoolean("bstats-enabled")) {
            return;
        }

        Metrics metrics = new Metrics(this, 18430);
        PluginContext.get().setMetrics(metrics);
    }

    public ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    public StreamService getStreamService() {
        return streamService;
    }

    public MysqlService getMysqlService() {
        return mysqlService;
    }
}
