package it.ax3lt.Main;

import it.ax3lt.BungeeManager.MessageListener;
import it.ax3lt.Utils.DiscordWebhook;
import it.ax3lt.Utils.StreamUtils;
import it.ax3lt.Commands.StreamCommandHandler;
import it.ax3lt.PlaceHolderApiExpansion.PlaceHolderManager;
import it.ax3lt.TabComplete.StreamCommandTabHandler;
import it.ax3lt.Utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;


public final class StreamAnnouncer extends JavaPlugin {

    public static boolean bungeeMode = false;

    @Override
    public void onEnable() {
        Objects.requireNonNull(getCommand("stream")).setTabCompleter(new StreamCommandTabHandler()); // Register tab completer for /stream command
        Objects.requireNonNull(getCommand("stream")).setExecutor(new StreamCommandHandler()); // Register command executor for /stream command

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) { // Check if PlaceholderAPI is installed
            new PlaceHolderManager().register();
        }
        saveDefaultConfig();


        // Register BungeeCord channel
        if (getConfig().getBoolean("bungee.enabled")) {
            bungeeMode = true;
            this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new MessageListener());
        }


        try {
            StreamUtils.configureParameters();
        } catch (IOException e) {

            getServer().getConsoleSender().sendMessage(Objects.requireNonNull(ConfigUtils.getConfigString(("parameters_error")))
                    .replace("%prefix%", Objects.requireNonNull(ConfigUtils.getConfigString("prefix")))
                    .replace("%message%", e.getMessage())
            );
            getServer().getPluginManager().disablePlugin(this);
        }

        getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            try {
                StreamUtils.refresh();
            } catch (IOException e) {
                getServer().getConsoleSender().sendMessage(Objects.requireNonNull(ConfigUtils.getConfigString("refresh_stream_error"))
                        .replace("%prefix%", Objects.requireNonNull(ConfigUtils.getConfigString("prefix")))
                        .replace("%message%", e.getMessage())
                );
            }
        }, 0L, getConfig().getLong("reload_time") * 20L);
    }

    @Override
    public void onDisable() {
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
        getServer().getScheduler().cancelTasks(this);
    }

    public static StreamAnnouncer getInstance() {
        return getPlugin(StreamAnnouncer.class);
    }
}


