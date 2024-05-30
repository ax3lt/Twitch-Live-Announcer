package it.ax3lt.Utils;

import com.google.gson.JsonObject;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import it.ax3lt.Main.TLA;
import it.ax3lt.Utils.Configs.ConfigUtils;
import it.ax3lt.Utils.Configs.MessagesConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.util.ArrayList;
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
                String userId;
                try {
                    userId = TwitchApi.getUserId(channel, token, client_id);
                    if (userId == null) {
                        Bukkit.getServer().getConsoleSender().sendMessage(Objects.requireNonNull(MessagesConfigUtils.getString("invalid_channel"))
                                .replace("%channel%", channel));
                        continue;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                JsonObject streamInfo;
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

                    // Execute customPlayer command
                    if (TLA.config.getBoolean("timedCommands.enabled")) {
                        List<String> commands = TLA.config.getStringList("timedCommands.live");
                        getLinkedUser(channel).forEach(user -> {
                            Bukkit.getScheduler().runTask(plugin, () -> {
                                for (String command : commands) {
                                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command
                                            .replace("%prefix%", Objects.requireNonNull(ConfigUtils.getConfigString("prefix")))
                                            .replace("%channel%", channel)
                                            .replace("%title%", streamTitle)
                                            .replace("%player%", user));
                                }
                            });
                        });
                    } 

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

    private static List<String> getLinkedUser(String channel) {
        Section linkedUsersSection = TLA.config.getSection("linked_users");
        List<String> linkedUsers = new ArrayList<>();

        if (linkedUsersSection != null) {
            for (Object key : linkedUsersSection.getKeys()) {
                List<String> linkedChannels = TLA.config.getStringList("linked_users." + key);
                if (linkedChannels.contains(channel)) {
                    linkedUsers.add(key.toString());
                }
            }
            List<String> onlinePlayers = new ArrayList<>();
            plugin.getServer().getOnlinePlayers().forEach(player -> {
                if(linkedUsers.contains(player.getName()))
                    onlinePlayers.add(player.getName());
            });
            return onlinePlayers;
        }
        return new ArrayList<>();
    }

}
