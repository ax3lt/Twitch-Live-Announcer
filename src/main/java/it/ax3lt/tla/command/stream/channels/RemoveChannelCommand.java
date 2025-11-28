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

public final class RemoveChannelCommand implements CommandExecutor {

    private final TwitchLiveAnnouncerPlugin plugin;

    public RemoveChannelCommand(TwitchLiveAnnouncerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("twitchliveannouncer.channels.remove")) {
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(MessageConfiguration.getMessage("remove_channel_usage"));
            return true;
        }

        String channelToRemove = args[2];
        List<String> channels = PluginContext.get().getConfiguration().getStringList("channels");
        if (channels.remove(channelToRemove)) {
            sender.sendMessage(MessageConfiguration.getMessage("channel-removed").replace("%channel%", channelToRemove));
        } else {
            sender.sendMessage(MessageConfiguration.getMessage("channel-not-added").replace("%channel%", channelToRemove));
        }

        PluginContext.get().getConfiguration().set("channels", channels);
        try {
            PluginContext.get().getConfiguration().save();
            PluginContext.get().getConfiguration().reload();
        } catch (IOException exception) {
            plugin.getLogger().warning("Unable to remove channel " + channelToRemove + ": " + exception.getMessage());
        }
        return true;
    }
}
