package it.ax3lt.tla.command.setchannel;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import it.ax3lt.tla.TwitchLiveAnnouncerPlugin;
import it.ax3lt.tla.config.MessageConfiguration;
import it.ax3lt.tla.core.PluginContext;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

public final class SetChannelCommand implements CommandExecutor {

    private final TwitchLiveAnnouncerPlugin plugin;

    public SetChannelCommand(TwitchLiveAnnouncerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageConfiguration.getMessage("mustbeplayer"));
            return true;
        }

        if (!sender.hasPermission("twitchliveannouncer.setchannel")) {
            sender.sendMessage(MessageConfiguration.getMessage("no_permission"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(MessageConfiguration.getMessage("setchannelusage"));
            return true;
        }

        Player player = (Player) sender;
        UUID playerUuid = player.getUniqueId();
        String channel = args[0];

        List<String> linkedUsers = new ArrayList<>(PluginContext.get().getConfiguration().getStringList("linked_users." + playerUuid));
        if (!sender.hasPermission("twitchliveannouncer.link.multiple") && !linkedUsers.isEmpty()) {
            sender.sendMessage(MessageConfiguration.getMessage("already-have-a-link"));
            return true;
        }

        Section linkedUsersSection = PluginContext.get().getConfiguration().getSection("linked_users");
        if (linkedUsersSection != null) {
            for (Object key : linkedUsersSection.getKeys()) {
                List<String> channels = PluginContext.get().getConfiguration().getStringList("linked_users." + key);
                for (String existing : channels) {
                    if (existing.equalsIgnoreCase(channel)) {
                        sender.sendMessage(MessageConfiguration.getMessage("channelalreadyset").replace("%channel%", channel));
                        return true;
                    }
                }
            }
        }

        List<String> channels = new ArrayList<>(PluginContext.get().getConfiguration().getStringList("channels"));
        List<String> lowerCaseChannels = channels.stream().map(value -> value.toLowerCase(Locale.ROOT)).collect(Collectors.toList());
        if (!lowerCaseChannels.contains(channel.toLowerCase(Locale.ROOT))) {
            channels.add(channel);
            PluginContext.get().getConfiguration().set("channels", channels);
        }

        for (String existing : linkedUsers) {
            if (existing.equalsIgnoreCase(channel)) {
                sender.sendMessage(MessageConfiguration.getMessage("link-already-made").replace("%channel%", channel));
                return true;
            }
        }

        linkedUsers.add(channel);
        PluginContext.get().getConfiguration().set("linked_users." + playerUuid, linkedUsers);

        try {
            PluginContext.get().getConfiguration().save();
            PluginContext.get().getConfiguration().reload();
        } catch (IOException exception) {
            plugin.getLogger().warning("Failed to set channel for player " + player.getName() + ": " + exception.getMessage());
            return true;
        }

        sender.sendMessage(MessageConfiguration.getMessage("channelset")
                .replace("%channel%", channel)
                .replace("%player%", player.getName()));
        return true;
    }
}
