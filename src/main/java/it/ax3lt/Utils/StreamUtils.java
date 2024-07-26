package it.ax3lt.Utils;

import com.google.gson.JsonObject;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import it.ax3lt.Main.TLA;
import it.ax3lt.Utils.Configs.ConfigUtils;
import it.ax3lt.Utils.Configs.MessagesConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.*;


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

                        // Remove channel from config
                        List<String> currentChannels = TLA.config.getStringList("channels"); // i fetch again so i can remove the channel and  not update the first List
                        currentChannels.remove(channel);
                        TLA.config.set("channels", currentChannels);
                        TLA.config.save();
                        TLA.config.reload();
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
                    doOfflineStream(channel);
                } else {
                    // Stream is online
                    String streamGameName = streamInfo.get("data").getAsJsonArray().get(0).getAsJsonObject().get("game_name").getAsString().toLowerCase();
                    String streamTitle = streamInfo.get("data").getAsJsonArray().get(0).getAsJsonObject().get("title").getAsString().toLowerCase();
                    String streamId = streamInfo.get("data").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsString();
                    doOnlineStream(channel, streamId, streamGameName, streamTitle);
                }
            }
        });
    }

    private static void doOfflineStream(String channel) {
        if (streams.containsKey(channel)) {
            streams.remove(channel);

            // Remove channel from database
            if (MysqlConnection.enabled) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        MysqlConnection.removeChannel(channel);
                    }
                }.runTaskAsynchronously(plugin);
            }

            if (!TLA.config.getBoolean("disable-not-streaming-message")) {
                MessageUtils.broadcastMessage(Objects.requireNonNull(MessagesConfigUtils.getString("not_streaming"))
                        .replace("%prefix%", Objects.requireNonNull(ConfigUtils.getConfigString("prefix")))
                        .replace("%channel%", channel), channel);
            }

            //Execute custom command
            if (TLA.config.getBoolean("commands.enabled")) {
                List<String> commands = TLA.config.getStringList("commands.stop");
                getLinkedUser(channel).forEach(user -> {
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                        for (String command : commands) {
                            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command
                                    .replace("%prefix%", Objects.requireNonNull(ConfigUtils.getConfigString("prefix")))
                                    .replace("%channel%", channel)
                                    .replace("%player%", user));
                        }
                    });
                });
            }
        }
    }

    private static void doOnlineStream(String channel, String streamId, String streamGameName, String streamTitle) {
        if (TLA.config.getBoolean("filter-stream-type.enabled") && TLA.config.getStringList("filter-stream-type.games")
                .stream()
                .map(String::toLowerCase)
                .noneMatch(streamGameName::contains)) {
            doOfflineStream(channel);
            return;
        }
        if (TLA.config.getBoolean("filter-stream-title.enabled") && TLA.config.getStringList("filter-stream-title.text")
                .stream()
                .map(String::toLowerCase)
                .noneMatch(streamTitle::contains)) {
            doOfflineStream(channel);
            return;
        }


        // Execute customPlayer command
        if (TLA.config.getBoolean("timedCommands.enabled")) {
            List<String> commands = TLA.config.getStringList("timedCommands.live");
            getLinkedUser(channel).forEach(user -> {
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
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


        // Prima era offline, ora è online -> aggiungo il canale alla lista
        if (!streams.containsKey(channel) || !streams.get(channel).equals(streamId)) {
            streams.put(channel, streamId);

            // Add channel to database
            if (MysqlConnection.enabled) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        MysqlConnection.addChannel(channel);
                    }
                }.runTaskAsynchronously(plugin);
            }

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
                getLinkedUser(channel).forEach(user -> {
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
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
        }
    }

    private static List<String> getLinkedUser(String channel) {
        Section linkedUsersSection = TLA.config.getSection("linked_users");
        List<String> linkedUsers = new ArrayList<>();

        if (linkedUsersSection != null) {
            for (Object key : linkedUsersSection.getKeys()) {
                UUID uuid = UUID.fromString(key.toString());
                List<String> linkedChannels = TLA.config.getStringList("linked_users." + uuid);
                if (linkedChannels.contains(channel)) {
                    linkedUsers.add(Bukkit.getOfflinePlayer(uuid).getName());
                }
            }
            List<String> onlinePlayers = new ArrayList<>();
            plugin.getServer().getOnlinePlayers().forEach(player -> {
                if (linkedUsers.contains(player.getName()))
                    onlinePlayers.add(player.getName());
            });
            return onlinePlayers;
        }
        return new ArrayList<>();
    }

}
