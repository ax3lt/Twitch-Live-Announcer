package it.ax3lt.Commands.Link;

import dev.dejvokep.boostedyaml.block.implementation.Section;
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


        Section linkedUsers = TLA.config.getSection("linked_users");;
        if (linkedUsers == null || linkedUsers.getKeys().isEmpty()) {
            sender.sendMessage(MessagesConfigUtils.getString("link-list-empty"));
            return true;
        }

        sender.sendMessage(MessagesConfigUtils.getString("show-links-header"));
        for (Object key : linkedUsers.getKeys()) {
            String playerName = (String) key;
            StringBuilder channels = new StringBuilder();
            List<String> linkedChannels = TLA.config.getStringList("linked_users." + key);
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
