package it.ax3lt.Main;

import it.ax3lt.Bstats.Metrics;
import it.ax3lt.BungeeManager.MessageListener;
import it.ax3lt.Commands.StreamCommandHandler;
import it.ax3lt.PlaceHolderApiExpansion.PlaceHolderManager;
import it.ax3lt.TabComplete.StreamCommandTabHandler;
import it.ax3lt.Utils.Configs.ConfigUtils;
import it.ax3lt.Utils.Configs.MessagesConfigUtils;
import it.ax3lt.Utils.StreamUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Objects;


public final class TLA extends JavaPlugin {

    public static boolean bungeeMode = false;
    Metrics m;

    @Override
    public void onEnable() {
        Objects.requireNonNull(getCommand("stream")).setTabCompleter(new StreamCommandTabHandler()); // Register tab completer for /stream command
        Objects.requireNonNull(getCommand("stream")).setExecutor(new StreamCommandHandler()); // Register command executor for /stream command

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) { // Check if PlaceholderAPI is installed
            new PlaceHolderManager().register();
        }
        saveDefaultConfig();
        MessagesConfigUtils.setup();

        if(getConfig().getBoolean("bstats-enabled"))
            m = new Metrics(this, 18430);



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


