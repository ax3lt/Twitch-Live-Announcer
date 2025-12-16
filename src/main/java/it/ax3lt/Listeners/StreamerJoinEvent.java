package it.ax3lt.Listeners;

import it.ax3lt.Classes.StreamData;
import it.ax3lt.Main.TLA;
import it.ax3lt.Utils.Configs.ConfigUtils;
import it.ax3lt.Utils.Configs.MessagesConfigUtils;
import it.ax3lt.Utils.MessageUtils;
import it.ax3lt.Utils.StreamUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class StreamerJoinEvent implements Listener {
    @EventHandler
    public void onStreamerJoin(PlayerJoinEvent event) {
        if (!TLA.config.getBoolean("announce-only-if-streamer-on-server")) {
            return;
        }
        Player p = event.getPlayer();
        UUID uuid = p.getUniqueId();

        List<String> channels = TLA.config.getStringList("linked_users." + uuid);
        if (channels != null && !channels.isEmpty()) {
            for (String channel : channels) {
                if(StreamUtils.getStreamQueue().containsKey(channel)) {

                    StreamData streamData = StreamUtils.getStreamQueue().get(channel);

                    if (streamData == null) {
                        TLA.getInstance().getLogger().warning("Stream data for channel " + channel + " is null.");
                        continue;
                    }

                    MessageUtils.broadcastMessage(Objects.requireNonNull(MessagesConfigUtils.getString("now_streaming"))
                                    .replace("%prefix%", Objects.requireNonNull(ConfigUtils.getConfigString("prefix")))
                                    .replace("%channel%", channel)
                                    .replace("%title%", streamData.getTitle())
                            , channel);

                    StreamUtils.dequeueStream(channel);
                    break;
                }
            }
        }

    }
}
