package it.ax3lt.Commands.Global;

import it.ax3lt.Main.TLA;
import it.ax3lt.Utils.Configs.ConfigUtils;
import it.ax3lt.Utils.Configs.MessagesConfigUtils;
import it.ax3lt.Utils.StreamUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class ListStatus implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("twitchliveannouncer.list"))
            return true;
        sender.sendMessage(Objects.requireNonNull(MessagesConfigUtils.getString("list-header")));
        List<String> channels = TLA.config.getStringList("channels");
        for (String channel : channels) {
            sender.sendMessage(Objects.requireNonNull(MessagesConfigUtils.getString("list-format"))
                    .replace("%channel%", channel)
                    .replace("%status%", StreamUtils.streams.containsKey(channel)
                            ? ConfigUtils.getConfigString("placeholders.live") : ConfigUtils.getConfigString("placeholders.offline")));
        }

        return true;
    }
}
