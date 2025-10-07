package it.ax3lt.tla.command.stream.link;

import it.ax3lt.tla.TwitchLiveAnnouncerPlugin;
import it.ax3lt.tla.config.MessageConfiguration;
import it.ax3lt.tla.core.PluginContext;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class RemoveLinkCommand implements CommandExecutor {

    private final TwitchLiveAnnouncerPlugin plugin;

    public RemoveLinkCommand(TwitchLiveAnnouncerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("twitchliveannouncer.link.remove")) {
            return true;
        }

        if (args.length <= 3) {
            sender.sendMessage(MessageConfiguration.getMessage("remove_link_usage"));
            return true;
        }

        String mcName = args[2];
        UUID playerUuid = Bukkit.getOfflinePlayer(mcName).getUniqueId();
        String twitchName = args[3];

        List<String> linkedUsers = new ArrayList<>(PluginContext.get().getConfiguration().getStringList("linked_users." + playerUuid));
        if (linkedUsers.isEmpty()) {
            sender.sendMessage(MessageConfiguration.getMessage("link-not-made")
                    .replace("%channel%", twitchName)
                    .replace("%player%", mcName));
            return true;
        }

        boolean removed = linkedUsers.removeIf(existing -> existing.equalsIgnoreCase(twitchName));
        if (!removed) {
            return true;
        }

        if (linkedUsers.isEmpty()) {
            PluginContext.get().getConfiguration().set("linked_users." + playerUuid, null);
        } else {
            PluginContext.get().getConfiguration().set("linked_users." + playerUuid, linkedUsers);
        }

        try {
            PluginContext.get().getConfiguration().save();
            PluginContext.get().getConfiguration().reload();
        } catch (IOException exception) {
            plugin.getLogger().warning("Failed to remove link for player " + mcName + ": " + exception.getMessage());
            return true;
        }

        sender.sendMessage(MessageConfiguration.getMessage("link-removed")
                .replace("%channel%", twitchName)
                .replace("%player%", mcName));
        return true;
    }
}
