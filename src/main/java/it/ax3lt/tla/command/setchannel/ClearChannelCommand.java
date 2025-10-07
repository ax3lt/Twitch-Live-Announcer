package it.ax3lt.tla.command.setchannel;

import it.ax3lt.tla.TwitchLiveAnnouncerPlugin;
import it.ax3lt.tla.config.MessageConfiguration;
import it.ax3lt.tla.core.PluginContext;
import it.ax3lt.tla.stream.StreamService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ClearChannelCommand implements CommandExecutor {

    private final TwitchLiveAnnouncerPlugin plugin;

    public ClearChannelCommand(TwitchLiveAnnouncerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageConfiguration.getMessage("mustbeplayer"));
            return true;
        }

        if (!sender.hasPermission("twitchliveannouncer.clearchannel")) {
            sender.sendMessage(MessageConfiguration.getMessage("no_permission"));
            return true;
        }

        if (args.length != 0) {
            sender.sendMessage(MessageConfiguration.getMessage("clearchannelusage"));
            return true;
        }

        Player player = (Player) sender;
        UUID playerUuid = player.getUniqueId();
        List<String> linkedUsers = new ArrayList<>(PluginContext.get().getConfiguration().getStringList("linked_users." + playerUuid));
        if (linkedUsers.isEmpty()) {
            sender.sendMessage(MessageConfiguration.getMessage("nolinkedchannel"));
            return true;
        }

        List<String> channels = new ArrayList<>(PluginContext.get().getConfiguration().getStringList("channels"));
        StreamService streamService = PluginContext.get().getStreamService();
        for (String channel : linkedUsers) {
            channels.remove(channel);
            streamService.markStreamOffline(channel);
        }

        PluginContext.get().getConfiguration().set("channels", channels);
        PluginContext.get().getConfiguration().set("linked_users." + playerUuid, null);
        try {
            PluginContext.get().getConfiguration().save();
            PluginContext.get().getConfiguration().reload();
        } catch (IOException exception) {
            plugin.getLogger().warning("Failed to clear channel for player " + player.getName() + ": " + exception.getMessage());
            return true;
        }

        sender.sendMessage(MessageConfiguration.getMessage("channelcleared"));
        return true;
    }
}
