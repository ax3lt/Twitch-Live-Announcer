package it.ax3lt.Utils;

import com.google.gson.JsonObject;
import it.ax3lt.Main.StreamAnnouncer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class StreamUtils {
    public static HashMap<String, String> streams = new HashMap<>();
    private static String client_id;
    private static String client_secret;
    private static String token;

    static StreamAnnouncer plugin;


    public static void configureParameters() throws IOException {
        client_id = ConfigUtils.getConfigString("client_id");
        client_secret = ConfigUtils.getConfigString("client_secret");
        token = TwitchApi.getToken(client_id, client_secret);
        plugin = StreamAnnouncer.getInstance();
    }

    public static void refresh() throws IOException {
        List<String> channels = plugin.getConfig().getStringList("channels");

        for (String channel : channels) {
            String userId = TwitchApi.getUserId(channel, token, client_id);
            JsonObject streamInfo = TwitchApi.getStreamInfo(userId, token, client_id);

            // Check stream status
            if (streamInfo.get("data").getAsJsonArray().size() == 0) {
                // Stream is offline
                if (streams.containsKey(channel)) {
                    streams.remove(channel);
                    if (!plugin.getConfig().getBoolean("disable-not-streaming-message")) {

                        MessageUtils.broadcastMessage(Objects.requireNonNull(ConfigUtils.getConfigString("not_streaming"))
                                .replace("%prefix%", Objects.requireNonNull(ConfigUtils.getConfigString("prefix")))
                                .replace("%channel%", channel), channel);
                    }

                    //Execute custom command
                    if (plugin.getConfig().getBoolean("commands.enabled")) {
                        List<String> commands = plugin.getConfig().getStringList("commands.stop");
                        for (String command : commands) {
                            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command
                                    .replace("%prefix%", Objects.requireNonNull(ConfigUtils.getConfigString("prefix")))
                                    .replace("%channel%", channel));
                        }
                    }
                }
            } else {
                // Stream is online
                String streamGameName = streamInfo.get("data").getAsJsonArray().get(0).getAsJsonObject().get("game_name").getAsString();
                String streamTitle = streamInfo.get("data").getAsJsonArray().get(0).getAsJsonObject().get("title").getAsString();


                if (plugin.getConfig().getBoolean("filter-stream-type.enabled") && plugin.getConfig().getStringList("filter-stream-type.games").stream().noneMatch(streamGameName::contains))
                    return;
                if (plugin.getConfig().getBoolean("filter-stream-title.enabled") && plugin.getConfig().getStringList("filter-stream-title.text").stream().noneMatch(streamTitle::contains))
                    return;

                String streamId = streamInfo.get("data").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsString();
                if (!streams.containsKey(channel) || !streams.get(channel).equals(streamId)) {
                    streams.put(channel, streamId);

                    MessageUtils.broadcastMessage(Objects.requireNonNull(ConfigUtils.getConfigString("now_streaming"))
                                    .replace("%prefix%", Objects.requireNonNull(ConfigUtils.getConfigString("prefix")))
                                    .replace("%channel%", channel)
                                    .replace("%title%", streamTitle)
                            , channel);


                    //Execute custom command
                    if (plugin.getConfig().getBoolean("commands.enabled")) {
                        List<String> commands = plugin.getConfig().getStringList("commands.start");
                        for (String command : commands) {
                            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command
                                    .replace("%prefix%", Objects.requireNonNull(ConfigUtils.getConfigString("prefix")))
                                    .replace("%channel%", channel)
                                    .replace("%title%", streamTitle));
                        }
                    }
                }
            }
        }
    }
}
