package it.ax3lt.tla.command.stream.channels;

import it.ax3lt.tla.TwitchLiveAnnouncerPlugin;
import it.ax3lt.tla.config.MessageConfiguration;
import it.ax3lt.tla.core.PluginContext;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public final class AddChannelCommand implements CommandExecutor {

    private final TwitchLiveAnnouncerPlugin plugin;

    public AddChannelCommand(TwitchLiveAnnouncerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("twitchliveannouncer.channels.add")) {
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(MessageConfiguration.getMessage("add_channel_usage"));
            return true;
        }

        List<String> channels = PluginContext.get().getConfiguration().getStringList("channels");
        String channelToAdd = args[2];
        List<String> lowerCaseChannels = channels.stream()
                .map(value -> value.toLowerCase(Locale.ROOT))
                .collect(Collectors.toList());

        if (lowerCaseChannels.contains(channelToAdd.toLowerCase(Locale.ROOT))) {
            sender.sendMessage(MessageConfiguration.getMessage("channel-already-added").replace("%channel%", channelToAdd));
        } else {
            channels.add(channelToAdd);
            sender.sendMessage(MessageConfiguration.getMessage("channel-added").replace("%channel%", channelToAdd));
            PluginContext.get().getConfiguration().set("channels", channels);
            try {
                PluginContext.get().getConfiguration().save();
                PluginContext.get().getConfiguration().reload();
            } catch (IOException exception) {
                plugin.getLogger().warning("Unable to add channel " + channelToAdd + ": " + exception.getMessage());
            }
        }
        return true;
    }
}
