package it.ax3lt.tla.command.stream.channels;

import it.ax3lt.tla.config.MessageConfiguration;
import it.ax3lt.tla.core.PluginContext;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class ListChannelCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("twitchliveannouncer.channels.list")) {
            return true;
        }

        List<String> channels = PluginContext.get().getConfiguration().getStringList("channels");
        if (channels.isEmpty()) {
            sender.sendMessage(MessageConfiguration.getMessage("channel-list-empty"));
            return true;
        }

        sender.sendMessage(MessageConfiguration.getMessage("show-channels-header"));
        for (String channel : channels) {
            sender.sendMessage(MessageConfiguration.getMessage("show-channels-format").replace("%channel%", channel));
        }
        return true;
    }
}
