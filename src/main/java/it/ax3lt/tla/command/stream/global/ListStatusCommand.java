package it.ax3lt.tla.command.stream.global;

import it.ax3lt.tla.config.ConfigurationFormatter;
import it.ax3lt.tla.config.MessageConfiguration;
import it.ax3lt.tla.core.PluginContext;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class ListStatusCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("twitchliveannouncer.list")) {
            return true;
        }

        sender.sendMessage(MessageConfiguration.getMessage("list-header"));
        List<String> channels = PluginContext.get().getConfiguration().getStringList("channels");
        for (String channel : channels) {
            String status = PluginContext.get().getStreamService().getActiveStreams().containsKey(channel)
                    ? ConfigurationFormatter.getConfigString("placeholders.live")
                    : ConfigurationFormatter.getConfigString("placeholders.offline");
            sender.sendMessage(MessageConfiguration.getMessage("list-format")
                    .replace("%channel%", channel)
                    .replace("%status%", status));
        }
        return true;
    }
}
