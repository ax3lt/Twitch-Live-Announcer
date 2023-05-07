package it.ax3lt.Commands.Channels;

import it.ax3lt.Main.StreamAnnouncer;
import it.ax3lt.Utils.ConfigUtils;
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

        List<String> channels = StreamAnnouncer.getInstance().getConfig().getStringList("channels");
        if (channels.isEmpty())
            sender.sendMessage(ConfigUtils.getConfigString("channels-list-empty"));
        else {
            sender.sendMessage(ConfigUtils.getConfigString("show-channels-header"));
            for (String channel : channels)
                sender.sendMessage(ConfigUtils.getConfigString("show-channels-format")
                        .replace("%channel%", channel));
        }
        return true;
    }
}
