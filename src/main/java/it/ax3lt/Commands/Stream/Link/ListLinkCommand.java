package it.ax3lt.Commands.Stream.Link;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import it.ax3lt.Main.TLA;
import it.ax3lt.Utils.Configs.MessagesConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ListLinkCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // /stream channels add <channel>
        //            0      1      2
        if (!sender.hasPermission("twitchliveannouncer.link.list"))
            return true;


        Section linkedUsers = TLA.config.getSection("linked_users");
        if (linkedUsers == null || linkedUsers.getKeys().isEmpty()) {
            sender.sendMessage(MessagesConfigUtils.getString("link-list-empty"));
            return true;
        }

        sender.sendMessage(MessagesConfigUtils.getString("show-links-header"));
        for (Object key : linkedUsers.getKeys()) {
            String playerUUID = (String) key;
            UUID uuid = UUID.fromString(playerUUID);
            String playerName = "";
            try {
                playerName = Objects.requireNonNull(Bukkit.getOfflinePlayer(uuid)).getName();
            } catch (Exception e) {
                playerName = "Unknown";
            }
            finally {
                if(playerName == null)
                    playerName = "Unknown";
            }

            StringBuilder channels = new StringBuilder();
            List<String> linkedChannels = TLA.config.getStringList("linked_users." + playerUUID);
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
