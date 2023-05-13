package it.ax3lt.Commands.Link;

import it.ax3lt.Main.TLA;
import it.ax3lt.Utils.Configs.MessagesConfigUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ListLinkCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // /stream channels add <channel>
        //            0      1      2
        if (!sender.hasPermission("twitchliveannouncer.link.list"))
            return true;

        ConfigurationSection linkedUsers = TLA.getInstance().getConfig().getConfigurationSection("linked_users");
        if (linkedUsers == null || linkedUsers.getKeys(false).size() == 0) {
            sender.sendMessage(MessagesConfigUtils.getString("no-linked-users"));
            return true;
        }

        sender.sendMessage(MessagesConfigUtils.getString("show-links-header"));
        for (String key : linkedUsers.getKeys(false)) {
            String playerName = key;
            StringBuilder channels = new StringBuilder();
            List<String> linkedChannels = TLA.getInstance().getConfig().getStringList("linked_users." + key);
            for (String channel : linkedChannels) {
                channels.append(channel).append(", ");
            }

            if(channels.length() == 0)
                continue;

            sender.sendMessage(MessagesConfigUtils.getString("show-links-format")
                    .replace("%player%", playerName)
                    .replace("%channels%", channels.substring(0, channels.toString().length() - 2)));
        }
        return true;
    }
}
