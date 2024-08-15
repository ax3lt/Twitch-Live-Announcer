package it.ax3lt.Main;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import it.ax3lt.Bstats.Metrics;
import it.ax3lt.BungeeManager.MessageListener;
import it.ax3lt.Commands.Stream.StreamCommandHandler;
import it.ax3lt.PlaceHolderApiExpansion.PlaceHolderManager;
import it.ax3lt.TabComplete.StreamCommandTabHandler;
import it.ax3lt.Utils.Configs.ConfigUtils;
import it.ax3lt.Utils.Configs.MessagesConfigUtils;
import it.ax3lt.Utils.MessageUtils;
import it.ax3lt.Utils.MysqlConnection;
import it.ax3lt.Utils.StreamUtils;
import it.ax3lt.Utils.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.Objects;


public final class TLA extends JavaPlugin {

    public static boolean bungeeMode = false;
    public static YamlDocument config;
    public static YamlDocument messages;
    Metrics m;

    @Override
    public void onEnable() {
        takeConfigBackup();
        registerConfig();
        if(!registerTwitchChecker()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if(!registerMysql()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        registerPlaceHolderApi();
        registerBstats();
        registerUpdateChecker();
        registerBungeeManager();
        registerCommands();
        registerTabCompleters();

        startTwitchCheckerRunnable();
        startMultiStreamRunnable();
    }


    @Override
    public void onDisable() {
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
    }

    private void takeConfigBackup() {
        if (getConfig().getBoolean("backupConfig.enabled")) {
            if (Objects.requireNonNull(getConfig().getString("backupConfig.when")).equalsIgnoreCase("daily")) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")).equals(Objects.requireNonNull(getConfig().getString("backupConfig.time")))) {
                            if (!backupFile("config.yml"))
                                getServer().getConsoleSender().sendMessage(Objects.requireNonNull(MessagesConfigUtils.getString("backup_error"))
                                        .replace("%prefix%", Objects.requireNonNull(ConfigUtils.getConfigString("prefix")))
                                        .replace("%file%", "config.yml")
                                );
                            if(!backupFile("messages.yml"))
                                getServer().getConsoleSender().sendMessage(Objects.requireNonNull(MessagesConfigUtils.getString("backup_error"))
                                        .replace("%prefix%", Objects.requireNonNull(ConfigUtils.getConfigString("prefix")))
                                        .replace("%file%", "messages.yml")
                                );
                        }
                    }
                }.runTaskTimerAsynchronously(this, 0L, 1200L);
            } else if (Objects.requireNonNull(getConfig().getString("backupConfig.when")).equalsIgnoreCase("startup")) {
                if (!backupFile("config.yml"))
                    getServer().getConsoleSender().sendMessage(Objects.requireNonNull(MessagesConfigUtils.getString("backup_error"))
                            .replace("%prefix%", Objects.requireNonNull(ConfigUtils.getConfigString("prefix")))
                            .replace("%file%", "config.yml")
                    );
                if(!backupFile("messages.yml"))
                    getServer().getConsoleSender().sendMessage(Objects.requireNonNull(MessagesConfigUtils.getString("backup_error"))
                            .replace("%prefix%", Objects.requireNonNull(ConfigUtils.getConfigString("prefix")))
                            .replace("%file%", "messages.yml")
                    );
            }
        }
    }

    private void registerConfig() {
        try {
            config = YamlDocument.create(new File(getDataFolder(), "config.yml"), Objects.requireNonNull(getResource("config.yml")), GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")).build());
            messages = YamlDocument.create(new File(getDataFolder(), "messages.yml"), Objects.requireNonNull(getResource("messages.yml")), GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("messages-version")).build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
    }

    private void registerUpdateChecker() {
        if (getConfig().getBoolean("check_updates"))
            new UpdateChecker().checkUpdate();
    }

    private void registerBstats() {
        if (getConfig().getBoolean("bstats-enabled"))
            m = new Metrics(this, 18430);
    }

    private void registerPlaceHolderApi() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) { // Check if PlaceholderAPI is installed
            new PlaceHolderManager().register();
        }
    }

    private boolean registerMysql() {
        if (getConfig().getBoolean("mysql.enabled")) {
            MysqlConnection.load();
            if (MysqlConnection.canConnect()) {
                MysqlConnection.setupTable();
                MysqlConnection.clearTable();
            } else {
                return false;
            }
        }
        return true;
    }


    private void registerBungeeManager() {
        // Register BungeeCord channel
        if (getConfig().getBoolean("bungee.enabled")) {
            bungeeMode = true;
            this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new MessageListener());
        }
    }

    private boolean registerTwitchChecker() {
        try {
            StreamUtils.configureParameters();
        } catch (IOException e) {

            getServer().getConsoleSender().sendMessage(Objects.requireNonNull(MessagesConfigUtils.getString(("parameters_error")))
                    .replace("%prefix%", Objects.requireNonNull(ConfigUtils.getConfigString("prefix")))
                    .replace("%message%", e.getMessage())
            );
            return false;
        }
        return true;
    }

    private void startTwitchCheckerRunnable() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    StreamUtils.refresh();
                } catch (IOException e) {
                    getServer().getConsoleSender().sendMessage(Objects.requireNonNull(MessagesConfigUtils.getString("refresh_stream_error"))
                            .replace("%prefix%", Objects.requireNonNull(ConfigUtils.getConfigString("prefix")))
                            .replace("%message%", e.getMessage())
                    );
                }
            }
        }.runTaskTimerAsynchronously(this, 0L, getConfig().getLong("reload_time") * 20L);
    }

    private void startMultiStreamRunnable() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (getConfig().getBoolean("multipleStreamService.enabled")) {
                    if (!StreamUtils.streams.isEmpty()) {
                        MessageUtils.broadcastMessage(Objects.requireNonNull(MessagesConfigUtils.getString("multi-stream"))
                                .replace("%prefix%", Objects.requireNonNull(ConfigUtils.getConfigString("prefix"))), "");
                    }
                }
            }
        }.runTaskTimerAsynchronously(this, 0L, getConfig().getLong("multipleStreamService.broadcastTime") * 20L);
    }

    private void registerCommands() {
        Objects.requireNonNull(getCommand("stream")).setExecutor(new StreamCommandHandler()); // Register command executor for /stream command
        Objects.requireNonNull(getCommand("setchannel")).setExecutor(new it.ax3lt.Commands.SetAndClearChannel.SetChannelCommand()); // Register command executor for /setchannel command
        Objects.requireNonNull(getCommand("clearchannel")).setExecutor(new it.ax3lt.Commands.SetAndClearChannel.ClearChannelCommand()); // Register command executor for /clearchannel command
    }

    private void registerTabCompleters() {
        Objects.requireNonNull(getCommand("stream")).setTabCompleter(new StreamCommandTabHandler()); // Register tab completer for /stream command
    }

    public static boolean backupFile(String filename) {
        if(!new File(TLA.getInstance().getDataFolder() + "/backups").exists()) {
            new File(TLA.getInstance().getDataFolder() + "/backups").mkdir();
        }
        File configFile = new File(TLA.getInstance().getDataFolder(), filename);
        // Backup filename: config.yml.18-12-2021_12-00-00
        // Folder path: backups/....
        String currentDateAndTime = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss"));
        if (!configFile.exists()) {
            return false;
        }
        try {
            java.nio.file.Files.copy(configFile.toPath(), new File(TLA.getInstance().getDataFolder() + "/backups", filename + "." + currentDateAndTime).toPath());
        } catch (IOException e) {
            TLA.getInstance().getLogger().severe("Failed to backup config file, error: " + e.getMessage());
            return false;
        }
        return true;
    }

    public static TLA getInstance() {
        return getPlugin(TLA.class);
    }
}


