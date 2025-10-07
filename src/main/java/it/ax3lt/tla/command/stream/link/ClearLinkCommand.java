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
import java.util.List;
import java.util.UUID;

public final class ClearLinkCommand implements CommandExecutor {

    private final TwitchLiveAnnouncerPlugin plugin;

    public ClearLinkCommand(TwitchLiveAnnouncerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("twitchliveannouncer.link.clear")) {
            return true;
        }

        if (args.length <= 2) {
            sender.sendMessage(MessageConfiguration.getMessage("clear_link_usage"));
            return true;
        }

        String mcName = args[2];
        UUID playerUuid = Bukkit.getOfflinePlayer(mcName).getUniqueId();
        List<String> linkedUsers = PluginContext.get().getConfiguration().getStringList("linked_users." + playerUuid);
        if (linkedUsers == null || linkedUsers.isEmpty()) {
            sender.sendMessage(MessageConfiguration.getMessage("link-list-empty"));
            return true;
        }

        PluginContext.get().getConfiguration().set("linked_users." + playerUuid, null);
        try {
            PluginContext.get().getConfiguration().save();
            PluginContext.get().getConfiguration().reload();
        } catch (IOException exception) {
            plugin.getLogger().warning("Failed to clear links for player " + mcName + ": " + exception.getMessage());
            return true;
        }

        sender.sendMessage(MessageConfiguration.getMessage("link-cleared").replace("%player%", mcName));
        return true;
    }
}
