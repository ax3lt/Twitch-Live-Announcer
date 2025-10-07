package it.ax3lt.tla.stream;

import com.google.gson.JsonObject;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import it.ax3lt.tla.TwitchLiveAnnouncerPlugin;
import it.ax3lt.tla.config.ConfigurationFormatter;
import it.ax3lt.tla.config.MessageConfiguration;
import it.ax3lt.tla.core.PluginContext;
import it.ax3lt.tla.database.MysqlService;
import it.ax3lt.tla.message.MessageService;
import it.ax3lt.tla.stream.model.StreamData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public final class StreamService {

    private final TwitchLiveAnnouncerPlugin plugin;
    private final Map<String, String> activeStreams = new HashMap<>();
    private final Map<String, StreamData> streamQueue = new HashMap<>();
    private String clientId;
    private String token;

    public StreamService(TwitchLiveAnnouncerPlugin plugin) {
        this.plugin = plugin;
    }

    public void configureParameters() throws IOException {
        clientId = PluginContext.get().getConfiguration().getString("client_id");
        String clientSecret = PluginContext.get().getConfiguration().getString("client_secret");
        token = TwitchApiClient.fetchToken(clientId, clientSecret);
    }

    public void refreshStreams() throws IOException {
        List<String> channels = PluginContext.get().getConfiguration().getStringList("channels");
        for (String channel : channels) {
            String userId = TwitchApiClient.fetchUserId(channel, token, clientId);
            if (userId == null) {
                plugin.getServer().getConsoleSender().sendMessage(MessageConfiguration.getMessage("invalid_channel").replace("%channel%", channel));
                removeChannelFromConfiguration(channel);
                continue;
            }

            JsonObject streamInfo = TwitchApiClient.fetchStreamInfo(userId, token, clientId);
            if (streamInfo.get("data").getAsJsonArray().isEmpty()) {
                handleOfflineStream(channel);
                continue;
            }

            JsonObject streamData = streamInfo.get("data").getAsJsonArray().get(0).getAsJsonObject();
            String streamGameName = streamData.get("game_name").getAsString().toLowerCase();
            String streamTitle = streamData.get("title").getAsString().toLowerCase();
            String streamId = streamData.get("id").getAsString();
            handleOnlineStream(channel, streamId, streamGameName, streamTitle);
        }
    }

    public Map<String, String> getActiveStreams() {
        return activeStreams;
    }

    public Map<String, StreamData> getStreamQueue() {
        return streamQueue;
    }

    public void dequeueStream(String channel) {
        streamQueue.remove(channel);
    }

    private void removeChannelFromConfiguration(String channel) {
        List<String> currentChannels = new ArrayList<>(PluginContext.get().getConfiguration().getStringList("channels"));
        currentChannels.remove(channel);
        PluginContext.get().getConfiguration().set("channels", currentChannels);
        try {
            PluginContext.get().getConfiguration().save();
            PluginContext.get().getConfiguration().reload();
        } catch (IOException exception) {
            plugin.getLogger().severe("Unable to update channels list: " + exception.getMessage());
        }
    }

    public void markStreamOffline(String channel) {
        handleOfflineStream(channel);
    }

    private void handleOfflineStream(String channel) {
        if (!activeStreams.containsKey(channel)) {
            return;
        }
        activeStreams.remove(channel);

        MysqlService mysqlService = PluginContext.get().getMysqlService();
        if (mysqlService != null && mysqlService.isEnabled()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    mysqlService.removeChannel(channel);
                }
            }.runTaskAsynchronously(plugin);
        }

        if (!PluginContext.get().getConfiguration().getBoolean("disable-not-streaming-message")) {
            if (PluginContext.get().getConfiguration().getBoolean("announce-only-if-streamer-on-server")) {
                dequeueStream(channel);
                List<String> users = getLinkedUsers(channel, true);
                if (!users.isEmpty()) {
                    MessageService.broadcastMessage(MessageConfiguration.getMessage("not_streaming").replace("%channel%", channel), channel);
                    return;
                }
            } else {
                MessageService.broadcastMessage(MessageConfiguration.getMessage("not_streaming").replace("%channel%", channel), channel);
            }
        }

        if (PluginContext.get().getConfiguration().getBoolean("commands.enabled")) {
            List<String> commands = PluginContext.get().getConfiguration().getStringList("commands.stop");
            getLinkedUsers(channel, PluginContext.get().getConfiguration().getBoolean("commands.skip_offline_players"))
                    .forEach(user -> executeCommands(commands, channel, "", user));
        }

        if (PluginContext.get().getConfiguration().getBoolean("channelCommands.enabled")) {
            List<String> commands = PluginContext.get().getConfiguration().getStringList("channelCommands." + channel + ".stop");
            getLinkedUsers(channel, PluginContext.get().getConfiguration().getBoolean("channelCommands.skip_offline_players"))
                    .forEach(user -> executeCommands(commands, channel, "", user));
        }
    }

    private void handleOnlineStream(String channel, String streamId, String streamGameName, String streamTitle) {
        if (PluginContext.get().getConfiguration().getBoolean("filter-stream-type.enabled")
                && PluginContext.get().getConfiguration().getStringList("filter-stream-type.games").stream()
                .map(String::toLowerCase)
                .noneMatch(streamGameName::contains)) {
            handleOfflineStream(channel);
            return;
        }

        if (PluginContext.get().getConfiguration().getBoolean("filter-stream-title.enabled")
                && PluginContext.get().getConfiguration().getStringList("filter-stream-title.text").stream()
                .map(String::toLowerCase)
                .noneMatch(streamTitle::contains)) {
            handleOfflineStream(channel);
            return;
        }

        if (PluginContext.get().getConfiguration().getBoolean("timedCommands.enabled")) {
            List<String> commands = PluginContext.get().getConfiguration().getStringList("timedCommands.live");
            getLinkedUsers(channel, PluginContext.get().getConfiguration().getBoolean("timedCommands.skip_offline_players"))
                    .forEach(user -> executeCommands(commands, channel, streamTitle, user));
        }

        boolean isNewStream = !activeStreams.containsKey(channel) || !Objects.equals(activeStreams.get(channel), streamId);
        activeStreams.put(channel, streamId);

        if (isNewStream) {
            MysqlService mysqlService = PluginContext.get().getMysqlService();
            if (mysqlService != null && mysqlService.isEnabled()) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        mysqlService.addChannel(channel);
                    }
                }.runTaskAsynchronously(plugin);
            }

            if (!PluginContext.get().getConfiguration().getBoolean("disable-streaming-message")) {
                if (PluginContext.get().getConfiguration().getBoolean("announce-only-if-streamer-on-server")) {
                    List<String> users = getLinkedUsers(channel, false);
                    boolean isStreamerOnline = plugin.getServer().getOnlinePlayers().stream()
                            .anyMatch(player -> users.contains(player.getName().toLowerCase()));
                    if (!isStreamerOnline) {
                        enqueueStream(channel, streamId, streamGameName, streamTitle);
                    } else {
                        MessageService.broadcastMessage(MessageConfiguration.getMessage("now_streaming")
                                .replace("%channel%", channel)
                                .replace("%title%", streamTitle), channel);
                    }
                } else {
                    MessageService.broadcastMessage(MessageConfiguration.getMessage("now_streaming")
                            .replace("%channel%", channel)
                            .replace("%title%", streamTitle), channel);
                }
            }

            if (PluginContext.get().getConfiguration().getBoolean("commands.enabled")) {
                List<String> commands = PluginContext.get().getConfiguration().getStringList("commands.start");
                getLinkedUsers(channel, PluginContext.get().getConfiguration().getBoolean("commands.skip_offline_players"))
                        .forEach(user -> executeCommands(commands, channel, streamTitle, user));
            }

            if (PluginContext.get().getConfiguration().getBoolean("channelCommands.enabled")) {
                List<String> commands = PluginContext.get().getConfiguration().getStringList("channelCommands." + channel + ".start");
                getLinkedUsers(channel, PluginContext.get().getConfiguration().getBoolean("channelCommands.skip_offline_players"))
                        .forEach(user -> executeCommands(commands, channel, streamTitle, user));
            }
        }
    }

    private void enqueueStream(String channel, String streamId, String streamGameName, String streamTitle) {
        streamQueue.putIfAbsent(channel, new StreamData(streamId, streamGameName, streamTitle));
    }

    private void executeCommands(List<String> commands, String channel, String streamTitle, String user) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (String command : commands) {
                String formattedCommand = command
                        .replace("%prefix%", ConfigurationFormatter.getConfigString("prefix"))
                        .replace("%channel%", channel)
                        .replace("%title%", streamTitle)
                        .replace("%player%", user);
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), formattedCommand);
            }
        });
    }

    private List<String> getLinkedUsers(String channel, boolean skipOfflinePlayers) {
        Section linkedUsersSection = PluginContext.get().getConfiguration().getSection("linked_users");
        List<String> linkedUsers = new ArrayList<>();
        String lowerCaseChannel = channel.toLowerCase();

        if (linkedUsersSection == null) {
            return linkedUsers;
        }

        for (Object key : linkedUsersSection.getKeys()) {
            UUID uuid = UUID.fromString(key.toString());
            List<String> linkedChannels = PluginContext.get().getConfiguration().getStringList("linked_users." + uuid);
            List<String> lowerCaseLinkedChannels = linkedChannels.stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
            if (lowerCaseLinkedChannels.contains(lowerCaseChannel)) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                if (offlinePlayer.getName() != null) {
                    linkedUsers.add(offlinePlayer.getName().toLowerCase());
                }
            }
        }

        if (skipOfflinePlayers) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(player -> player.getName().toLowerCase())
                    .filter(linkedUsers::contains)
                    .collect(Collectors.toList());
        }
        return linkedUsers;
    }
}
