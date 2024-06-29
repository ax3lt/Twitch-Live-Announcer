package it.ax3lt.Commands.Stream.Channels;

import it.ax3lt.Main.TLA;
import it.ax3lt.Utils.Configs.MessagesConfigUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ListChannelCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // /stream channels add <channel>
        //            0      1      2
        if (!sender.hasPermission("twitchliveannouncer.channels.list"))
            return true;

        List<String> channels = TLA.config.getStringList("channels");
        if (channels.isEmpty())
            sender.sendMessage(MessagesConfigUtils.getString("channel-list-empty"));
        else {
            sender.sendMessage(MessagesConfigUtils.getString("show-channels-header"));
            for (String channel : channels)
                sender.sendMessage(MessagesConfigUtils.getString("show-channels-format")
                        .replace("%channel%", channel));
        }
        return true;
    }
}
