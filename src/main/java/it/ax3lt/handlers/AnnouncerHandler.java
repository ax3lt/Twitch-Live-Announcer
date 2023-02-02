package it.ax3lt.handlers;

import com.google.gson.JsonObject;
import it.ax3lt.Utils.ConfigUtils;
import it.ax3lt.Utils.StreamUtils;
import it.ax3lt.main.StreamAnnouncer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class AnnouncerHandler {
    public static HashMap<String, String> streams = new HashMap<>();
    private static String client_id;
    private static String client_secret;
    private static String token;

    static StreamAnnouncer plugin;


    public static void configureParameters() throws IOException {
        client_id = ConfigUtils.getConfigString("client_id");
        client_secret = ConfigUtils.getConfigString("client_secret");
        token = StreamUtils.getToken(client_id, client_secret);
        plugin = StreamAnnouncer.getInstance();
    }

    public static void refresh() throws IOException {
        List<String> channels = plugin.getConfig().getStringList("channels");

        for (String channel : channels) {
            String userId = StreamUtils.getUserId(channel, token, client_id);
            JsonObject streamInfo = StreamUtils.getStreamInfo(userId, token, client_id);

            if (streamInfo.get("data").getAsJsonArray().size() == 0) {
                if (streams.containsKey(channel)) {
                    streams.remove(channel);
                    plugin.getServer().broadcastMessage(
                            Objects.requireNonNull(ConfigUtils.getConfigString("not_streaming"))
                                    .replace("%prefix%", Objects.requireNonNull(ConfigUtils.getConfigString("prefix")))
                                    .replace("%channel%", channel)
                    );
                }
            } else {
                String streamTitle = streamInfo.get("data").getAsJsonArray().get(0).getAsJsonObject().get("title").getAsString();
                String streamId = streamInfo.get("data").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsString();
                if (!streams.containsKey(channel) || !streams.get(channel).equals(streamId)) {
                    streams.put(channel, streamId);
                    plugin.getServer().broadcastMessage(
                            Objects.requireNonNull(ConfigUtils.getConfigString("now_streaming"))
                                    .replace("%prefix%", Objects.requireNonNull(ConfigUtils.getConfigString("prefix")))
                                    .replace("%channel%", channel)
                                    .replace("%title%", streamTitle)
                    );
                }
            }
        }
    }
}
