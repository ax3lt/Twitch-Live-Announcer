package it.ax3lt.Utils;

import com.google.gson.JsonObject;
import it.ax3lt.Main.TLA;
import it.ax3lt.Utils.Configs.ConfigUtils;
import it.ax3lt.Utils.Configs.MessagesConfigUtils;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class StreamUtils {
    public static HashMap<String, String> streams = new HashMap<>();
    private static String client_id;
    private static String token;

    static TLA plugin;


    public static void configureParameters() throws IOException {
        client_id = ConfigUtils.getConfigString("client_id");
        String client_secret = ConfigUtils.getConfigString("client_secret");
        token = TwitchApi.getToken(client_id, client_secret);
        plugin = TLA.getInstance();
    }

    public static void refresh() throws IOException {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            List<String> channels = TLA.config.getStringList("channels");

            for (String channel : channels) {
                String userId = null;
                try {
                    userId = TwitchApi.getUserId(channel, token, client_id);
                    if(userId == null)
                    {
                        Bukkit.getServer().getConsoleSender().sendMessage(Objects.requireNonNull(MessagesConfigUtils.getString("invalid_channel"))
                                .replace("%channel%", channel));
                        continue;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                JsonObject streamInfo = null;
                try {
                    streamInfo = TwitchApi.getStreamInfo(userId, token, client_id);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                // Check stream status
                if (streamInfo.get("data").getAsJsonArray().isEmpty()) {
                    // Stream is offline
                    if (streams.containsKey(channel)) {
                        streams.remove(channel);
                        if (!TLA.config.getBoolean("disable-not-streaming-message")) {

                            MessageUtils.broadcastMessage(Objects.requireNonNull(MessagesConfigUtils.getString("not_streaming"))
                                    .replace("%prefix%", Objects.requireNonNull(ConfigUtils.getConfigString("prefix")))
                                    .replace("%channel%", channel), channel);
                        }

                        //Execute custom command
                        if (TLA.config.getBoolean("commands.enabled")) {
                            List<String> commands = TLA.config.getStringList("commands.stop");
                            Bukkit.getScheduler().runTask(plugin, () -> {
                                for (String command : commands) {
                                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command
                                            .replace("%prefix%", Objects.requireNonNull(ConfigUtils.getConfigString("prefix")))
                                            .replace("%channel%", channel));
                                }
                            });
                        }
                    }
                } else {
                    // Stream is online
                    String streamGameName = streamInfo.get("data").getAsJsonArray().get(0).getAsJsonObject().get("game_name").getAsString();
                    String streamTitle = streamInfo.get("data").getAsJsonArray().get(0).getAsJsonObject().get("title").getAsString();


                    if (TLA.config.getBoolean("filter-stream-type.enabled") && TLA.config.getStringList("filter-stream-type.games").stream().noneMatch(streamGameName::contains))
                        return;
                    if (TLA.config.getBoolean("filter-stream-title.enabled") && TLA.config.getStringList("filter-stream-title.text").stream().noneMatch(streamTitle::contains))
                        return;

                    String streamId = streamInfo.get("data").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsString();
                    if (!streams.containsKey(channel) || !streams.get(channel).equals(streamId)) {
                        streams.put(channel, streamId);

                        if (!TLA.config.getBoolean("disable-streaming-message")) {
                            MessageUtils.broadcastMessage(Objects.requireNonNull(MessagesConfigUtils.getString("now_streaming"))
                                            .replace("%prefix%", Objects.requireNonNull(ConfigUtils.getConfigString("prefix")))
                                            .replace("%channel%", channel)
                                            .replace("%title%", streamTitle)
                                    , channel);


                        }
                        //Execute custom command
                        if (TLA.config.getBoolean("commands.enabled")) {
                            List<String> commands = TLA.config.getStringList("commands.start");
                            Bukkit.getScheduler().runTask(plugin, () -> {
                                for (String command : commands) {
                                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command
                                            .replace("%prefix%", Objects.requireNonNull(ConfigUtils.getConfigString("prefix")))
                                            .replace("%channel%", channel)
                                            .replace("%title%", streamTitle));
                                }
                            });
                        }
                    }
                }
            }
        });
    }
}
