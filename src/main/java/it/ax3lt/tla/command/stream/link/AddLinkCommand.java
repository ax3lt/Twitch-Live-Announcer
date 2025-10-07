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
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

public final class AddLinkCommand implements CommandExecutor {

    private final TwitchLiveAnnouncerPlugin plugin;

    public AddLinkCommand(TwitchLiveAnnouncerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("twitchliveannouncer.link.add")) {
            return true;
        }

        if (args.length < 4) {
            sender.sendMessage(MessageConfiguration.getMessage("add_link_usage"));
            return true;
        }

        String mcName = args[2];
        UUID playerUuid = Bukkit.getOfflinePlayer(mcName).getUniqueId();
        String twitchName = args[3];

        List<String> linkedUsers = new ArrayList<>(PluginContext.get().getConfiguration().getStringList("linked_users." + playerUuid));
        if (!sender.hasPermission("twitchliveannouncer.link.multiple") && !linkedUsers.isEmpty()) {
            sender.sendMessage(MessageConfiguration.getMessage("already-have-a-link"));
            return true;
        }

        List<String> channels = new ArrayList<>(PluginContext.get().getConfiguration().getStringList("channels"));
        String lowerTwitchName = twitchName.toLowerCase(Locale.ROOT);
        List<String> lowerCaseChannels = channels.stream().map(value -> value.toLowerCase(Locale.ROOT)).collect(Collectors.toList());
        if (!lowerCaseChannels.contains(lowerTwitchName)) {
            channels.add(twitchName);
            PluginContext.get().getConfiguration().set("channels", channels);
        }

        for (String existing : linkedUsers) {
            if (existing.equalsIgnoreCase(twitchName)) {
                sender.sendMessage(MessageConfiguration.getMessage("link-already-made").replace("%channel%", twitchName));
                return true;
            }
        }

        linkedUsers.add(twitchName);
        PluginContext.get().getConfiguration().set("linked_users." + playerUuid, linkedUsers);

        try {
            PluginContext.get().getConfiguration().save();
            PluginContext.get().getConfiguration().reload();
        } catch (IOException exception) {
            plugin.getLogger().warning("Failed to save link for player " + mcName + ": " + exception.getMessage());
            return true;
        }

        sender.sendMessage(MessageConfiguration.getMessage("link-made")
                .replace("%channel%", twitchName)
                .replace("%player%", mcName));
        return true;
    }
}
