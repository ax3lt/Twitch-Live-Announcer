package it.ax3lt.tla.command.stream.link;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import it.ax3lt.tla.config.MessageConfiguration;
import it.ax3lt.tla.core.PluginContext;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public final class ListLinkCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("twitchliveannouncer.link.list")) {
            return true;
        }

        Section linkedUsers = PluginContext.get().getConfiguration().getSection("linked_users");
        if (linkedUsers == null || linkedUsers.getKeys().isEmpty()) {
            sender.sendMessage(MessageConfiguration.getMessage("link-list-empty"));
            return true;
        }

        sender.sendMessage(MessageConfiguration.getMessage("show-links-header"));
        for (Object key : linkedUsers.getKeys()) {
            String playerId = (String) key;
            UUID uuid = UUID.fromString(playerId);
            String playerName = Bukkit.getOfflinePlayer(uuid).getName();
            if (playerName == null) {
                playerName = "Unknown";
            }

            List<String> linkedChannels = PluginContext.get().getConfiguration().getStringList("linked_users." + playerId);
            if (linkedChannels.isEmpty()) {
                continue;
            }

            String channels = String.join(", ", linkedChannels);
            sender.sendMessage(MessageConfiguration.getMessage("show-links-format")
                    .replace("%player%", playerName)
                    .replace("%channels%", channels));
        }
        return true;
    }
}
