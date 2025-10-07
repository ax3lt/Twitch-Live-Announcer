package it.ax3lt.tla.listener;

import it.ax3lt.tla.config.MessageConfiguration;
import it.ax3lt.tla.core.PluginContext;
import it.ax3lt.tla.message.MessageService;
import it.ax3lt.tla.stream.StreamService;
import it.ax3lt.tla.stream.model.StreamData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;
import java.util.UUID;

public final class StreamerJoinListener implements Listener {

    @EventHandler
    public void onStreamerJoin(PlayerJoinEvent event) {
        if (!PluginContext.get().getConfiguration().getBoolean("announce-only-if-streamer-on-server")) {
            return;
        }

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        List<String> channels = PluginContext.get().getConfiguration().getStringList("linked_users." + uuid);
        if (channels == null || channels.isEmpty()) {
            return;
        }

        StreamService streamService = PluginContext.get().getStreamService();
        for (String channel : channels) {
            StreamData streamData = streamService.getStreamQueue().get(channel);
            if (streamData == null) {
                continue;
            }

            MessageService.broadcastMessage(
                    MessageConfiguration.getMessage("now_streaming")
                            .replace("%channel%", channel)
                            .replace("%title%", streamData.getTitle()),
                    channel
            );
            streamService.dequeueStream(channel);
            break;
        }
    }
}
