package it.ax3lt.Commands.Link;

import it.ax3lt.Main.StreamAnnouncer;
import it.ax3lt.Utils.ConfigUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class ListLinkCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // /stream channels add <channel>
        //            0      1      2
        if (!sender.hasPermission("twitchliveannouncer.link.list"))
            return true;

        ConfigurationSection linkedUsers = StreamAnnouncer.getInstance().getConfig().getConfigurationSection("linked_users");
        if (linkedUsers == null || linkedUsers.getKeys(false).size() == 0) {
            sender.sendMessage(ConfigUtils.getConfigString("no-linked-users"));
            return true;
        }

        sender.sendMessage(ConfigUtils.getConfigString("show-links-header"));
        for (String key : linkedUsers.getKeys(false)) {
            String playerName = key;
            StringBuilder channels = new StringBuilder();
            List<String> linkedChannels = StreamAnnouncer.getInstance().getConfig().getStringList("linked_users." + key);
            for (String channel : linkedChannels) {
                channels.append(channel).append(", ");
            }

            if(channels.length() == 0)
                continue;

            sender.sendMessage(ConfigUtils.getConfigString("show-links-format")
                    .replace("%player%", playerName)
                    .replace("%channels%", channels.substring(0, channels.toString().length() - 2)));
        }
        return true;
    }
}
