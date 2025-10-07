package it.ax3lt.tla.stream;

import it.ax3lt.tla.TwitchLiveAnnouncerPlugin;
import it.ax3lt.tla.config.MessageConfiguration;
import it.ax3lt.tla.core.PluginContext;
import it.ax3lt.tla.message.MessageService;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;

public final class StreamScheduler {

    private final TwitchLiveAnnouncerPlugin plugin;
    private final StreamService streamService;

    public StreamScheduler(TwitchLiveAnnouncerPlugin plugin, StreamService streamService) {
        this.plugin = plugin;
        this.streamService = streamService;
    }

    public void start() {
        startRefreshTask();
        startMultiStreamTask();
    }

    private void startRefreshTask() {
        long intervalTicks = PluginContext.get().getConfiguration().getLong("reload_time") * 20L;
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    streamService.refreshStreams();
                } catch (IOException exception) {
                    plugin.getServer().getConsoleSender().sendMessage(
                            MessageConfiguration.getMessage("refresh_stream_error")
                                    .replace("%message%", exception.getMessage())
                    );
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0L, Math.max(intervalTicks, 20L));
    }

    private void startMultiStreamTask() {
        long intervalTicks = PluginContext.get().getConfiguration().getLong("multipleStreamService.broadcastTime") * 20L;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!PluginContext.get().getConfiguration().getBoolean("multipleStreamService.enabled")) {
                    return;
                }
                if (!streamService.getActiveStreams().isEmpty()) {
                    MessageService.broadcastMessage(
                            MessageConfiguration.getMessage("multi-stream"),
                            ""
                    );
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0L, Math.max(intervalTicks, 20L));
    }
}
