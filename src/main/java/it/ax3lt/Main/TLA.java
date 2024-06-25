package it.ax3lt.Main;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import it.ax3lt.Bstats.Metrics;
import it.ax3lt.BungeeManager.MessageListener;
import it.ax3lt.Commands.StreamCommandHandler;
import it.ax3lt.PlaceHolderApiExpansion.PlaceHolderManager;
import it.ax3lt.TabComplete.StreamCommandTabHandler;
import it.ax3lt.Utils.Configs.ConfigUtils;
import it.ax3lt.Utils.Configs.MessagesConfigUtils;
import it.ax3lt.Utils.MessageUtils;
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

        try {
            config = YamlDocument.create(new File(getDataFolder(), "config.yml"), Objects.requireNonNull(getResource("config.yml")), GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")).build());
            messages = YamlDocument.create(new File(getDataFolder(), "messages.yml"), Objects.requireNonNull(getResource("messages.yml")), GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("messages-version")).build());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        Objects.requireNonNull(getCommand("stream")).setTabCompleter(new StreamCommandTabHandler()); // Register tab completer for /stream command
        Objects.requireNonNull(getCommand("stream")).setExecutor(new StreamCommandHandler()); // Register command executor for /stream command

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) { // Check if PlaceholderAPI is installed
            new PlaceHolderManager().register();
        }

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        if (getConfig().getBoolean("bstats-enabled"))
            m = new Metrics(this, 18430);

        if (getConfig().getBoolean("check_updates"))
            new UpdateChecker().checkUpdate();


        // Register BungeeCord channel
        if (getConfig().getBoolean("bungee.enabled")) {
            bungeeMode = true;
            this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new MessageListener());
        }


        try {
            StreamUtils.configureParameters();
        } catch (IOException e) {

            getServer().getConsoleSender().sendMessage(Objects.requireNonNull(MessagesConfigUtils.getString(("parameters_error")))
                    .replace("%prefix%", Objects.requireNonNull(ConfigUtils.getConfigString("prefix")))
                    .replace("%message%", e.getMessage())
            );
            getServer().getPluginManager().disablePlugin(this);
        }

        getServer().getScheduler().scheduleAsyncRepeatingTask(this, () -> {
            try {
                StreamUtils.refresh();
            } catch (IOException e) {
                getServer().getConsoleSender().sendMessage(Objects.requireNonNull(MessagesConfigUtils.getString("refresh_stream_error"))
                        .replace("%prefix%", Objects.requireNonNull(ConfigUtils.getConfigString("prefix")))
                        .replace("%message%", e.getMessage())
                );
            }
        }, 0L, getConfig().getLong("reload_time") * 20L);


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

    @Override
    public void onDisable() {
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
        getServer().getScheduler().cancelTasks(this);
    }

    public static TLA getInstance() {
        return getPlugin(TLA.class);
    }
}


