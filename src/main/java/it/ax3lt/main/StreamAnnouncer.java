package it.ax3lt.main;

import it.ax3lt.handlers.AnnouncerHandler;
import it.ax3lt.handlers.CommandHandler;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Objects;


public final class StreamAnnouncer extends JavaPlugin {

    @Override
    public void onEnable() {

        Objects.requireNonNull(getCommand("stream")).setExecutor(new CommandHandler());

        saveDefaultConfig();
        try {
            AnnouncerHandler.configureParameters();
        } catch (IOException e) {

            getServer().getConsoleSender().sendMessage(Objects.requireNonNull(getConfig().getString("parameters_error"))
                    .replace("%prefix", Objects.requireNonNull(getConfig().getString("prefix")))
                    .replace("%message%", e.getMessage())
            );
            getServer().getPluginManager().disablePlugin(this);
        }

        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                try {
                    AnnouncerHandler.refresh();
                } catch (IOException e) {
                    getServer().getConsoleSender().sendMessage(Objects.requireNonNull(getConfig().getString("refresh_stream_error"))
                            .replace("%prefix", Objects.requireNonNull(getConfig().getString("prefix")))
                            .replace("%message%", e.getMessage())
                    );
                }
            }
        }, 0L, getConfig().getLong("reload_time") * 20L);
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
    }

    public static StreamAnnouncer getInstance() {
        return getPlugin(StreamAnnouncer.class);
    }
}


