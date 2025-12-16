package it.ax3lt.Utils;

import com.google.gson.JsonObject;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import it.ax3lt.Classes.StreamData;
import it.ax3lt.Main.TLA;
import it.ax3lt.Utils.Configs.ConfigUtils;
import it.ax3lt.Utils.Configs.MessagesConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class StreamUtils {

    private static HashMap<String, StreamData> streams = new HashMap<>();
    private static HashMap<String, StreamData> streamQueue = new HashMap<>();
    private static String client_id;
    private static String token;

    public static HashMap<String, StreamData> getStreams() {
        return streams;
    }

    public static HashMap<String, StreamData> getStreamQueue() {
        return streamQueue;
    }

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
                        List<String> currentChannels = TLA.config.getStringList("channels");
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
                    StreamData data = new StreamData(streamId, streamGameName, streamTitle);
                    doOnlineStream(channel, data);
                }
            }
        });
    }

    public static void doOfflineStream(String channel) {
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
                if (TLA.config.getBoolean("announce-only-if-streamer-on-server")) {
                    dequeueStream(channel);
                    // Check if the streamer is online
                    List<String> users = getLinkedUser(channel, true);
                    if(!users.isEmpty()) {
                            MessageUtils.broadcastMessage(Objects.requireNonNull(MessagesConfigUtils.getString("not_streaming"))
                                    .replace("%prefix%", Objects.requireNonNull(ConfigUtils.getConfigString("prefix")))
                                    .replace("%channel%", channel), channel);
                            return;
                    }
                } else {
                    MessageUtils.broadcastMessage(Objects.requireNonNull(MessagesConfigUtils.getString("not_streaming"))
                            .replace("%prefix%", Objects.requireNonNull(ConfigUtils.getConfigString("prefix")))
                            .replace("%channel%", channel), channel);
                }
            }

            // Execute custom command
            if (TLA.config.getBoolean("commands.enabled")) {
                List<String> commands = TLA.config.getStringList("commands.stop");
                getLinkedUser(channel, TLA.config.getBoolean("commands.skip_offline_players")).forEach(user -> {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        for (String command : commands) {
                            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command
                                    .replace("%prefix%", Objects.requireNonNull(ConfigUtils.getConfigString("prefix")))
                                    .replace("%channel%", channel)
                                    .replace("%player%", user));
                        }
                    });
                });
            }

            if (TLA.config.getBoolean("channelCommands.enabled")) {
                List<String> stopCommands = TLA.config.getStringList("channelCommands." + channel + ".stop");
                getLinkedUser(channel, TLA.config.getBoolean("channelCommands.skip_offline_players")).forEach(user -> {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        for (String command : stopCommands) {
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

    public static void doOnlineStream(String channel, StreamData data) {
        if (TLA.config.getBoolean("filter-stream-type.enabled") && TLA.config.getStringList("filter-stream-type.games")
                .stream()
                .map(String::toLowerCase)
                .noneMatch(data.getGameName()::contains)) {
            doOfflineStream(channel);
            return;
        }
        if (TLA.config.getBoolean("filter-stream-title.enabled") && TLA.config.getStringList("filter-stream-title.text")
                .stream()
                .map(String::toLowerCase)
                .noneMatch(data.getTitle()::contains)) {
            doOfflineStream(channel);
            return;
        }

//        // Execute customPlayer command
//        if (TLA.config.getBoolean("timedCommands.enabled")) {
//            List<String> commands = TLA.config.getStringList("timedCommands.live");
//            getLinkedUser(channel, TLA.config.getBoolean("timedCommands.skip_offline_players")).forEach(user -> {
//                Bukkit.getScheduler().runTask(plugin, () -> {
//                    for (String command : commands) {
//                        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command
//                                .replace("%prefix%", Objects.requireNonNull(ConfigUtils.getConfigString("prefix")))
//                                .replace("%channel%", channel)
//                                .replace("%title%", data.getTitle())
//                                .replace("%player%", user));
//                    }
//                });
//            });
//        }

        // Prima era offline, ora è online -> aggiungo il canale alla lista
        if (!streams.containsKey(channel) || !streams.get(channel).equals(data.getStreamId())) {
            streams.put(channel, data);

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
                if (TLA.config.getBoolean("announce-only-if-streamer-on-server")) {
                    // check if the streamer is online
                    List<String> users = getLinkedUser(channel, false);
                    boolean isStreamerOnline = plugin.getServer().getOnlinePlayers().stream()
                            .anyMatch(player -> users.contains(player.getName().toLowerCase()));
                    if (!isStreamerOnline) {
                        enqueueStream(channel, data);
                    } else {
                        MessageUtils.broadcastMessage(Objects.requireNonNull(MessagesConfigUtils.getString("now_streaming"))
                                        .replace("%prefix%", Objects.requireNonNull(ConfigUtils.getConfigString("prefix")))
                                        .replace("%channel%", channel)
                                        .replace("%title%", data.getTitle())
                                , channel);
                    }
                } else {
                    MessageUtils.broadcastMessage(Objects.requireNonNull(MessagesConfigUtils.getString("now_streaming"))
                                    .replace("%prefix%", Objects.requireNonNull(ConfigUtils.getConfigString("prefix")))
                                    .replace("%channel%", channel)
                                    .replace("%title%", data.getTitle())
                            , channel);
                }
            }

            if (TLA.config.getBoolean("commands.enabled")) {
                List<String> commands = TLA.config.getStringList("commands.start");
                getLinkedUser(channel, TLA.config.getBoolean("commands.skip_offline_players")).forEach(user -> {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        for (String command : commands) {
                            String finalCommand = command
                                    .replace("%prefix%", Objects.requireNonNull(ConfigUtils.getConfigString("prefix")))
                                    .replace("%channel%", channel)
                                    .replace("%title%", data.getTitle())
                                    .replace("%player%", user);
                            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), finalCommand);
                        }
                    });
                });
            }

            if (TLA.config.getBoolean("channelCommands.enabled")) {
                List<String> startCommands = TLA.config.getStringList("channelCommands." + channel + ".start");
                getLinkedUser(channel, TLA.config.getBoolean("channelCommands.skip_offline_players")).forEach(user -> {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        for (String command : startCommands) {
                            String finalCommand = command
                                    .replace("%prefix%", Objects.requireNonNull(ConfigUtils.getConfigString("prefix")))
                                    .replace("%channel%", channel)
                                    .replace("%title%", data.getTitle())
                                    .replace("%player%", user);
                            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), finalCommand);
                        }
                    });
                });
            }
        }
    }

    public static List<String> getLinkedUser(String channel, boolean skipOfflinePlayers) {
        Section linkedUsersSection = TLA.config.getSection("linked_users");
        List<String> linkedUsers = new ArrayList<>();
        String lowerCaseChannel = channel.toLowerCase();

        if (linkedUsersSection != null) {
            for (Object key : linkedUsersSection.getKeys()) {
                UUID uuid = UUID.fromString(key.toString());
                List<String> linkedChannels = TLA.config.getStringList("linked_users." + uuid);

                // Convert linkedChannels to lowercase for case-insensitive comparison
                List<String> lowerCaseLinkedChannels = linkedChannels.stream()
                        .map(String::toLowerCase)
                        .collect(Collectors.toList());

                if (lowerCaseLinkedChannels.contains(lowerCaseChannel)) {
                    String playerName = Bukkit.getOfflinePlayer(uuid).getName();
                    if (playerName != null) {
                        linkedUsers.add(playerName.toLowerCase());
                    }
                }
            }

            if (skipOfflinePlayers) {
                List<String> onlinePlayers = new ArrayList<>();
                plugin.getServer().getOnlinePlayers().forEach(player -> {
                    if (linkedUsers.contains(player.getName().toLowerCase())) {
                        onlinePlayers.add(player.getName());
                    }
                });
                return onlinePlayers;
            }

            return linkedUsers;
        }
        return new ArrayList<>();
    }

    private static void enqueueStream(String channel, StreamData data) {
        if (!streamQueue.containsKey(channel)) {
            streamQueue.put(channel, data);
        }
    }

    public static void dequeueStream(String channel) {
        streamQueue.remove(channel);
    }
}
