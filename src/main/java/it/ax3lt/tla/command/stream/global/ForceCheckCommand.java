package it.ax3lt.tla.command.stream.global;

import it.ax3lt.tla.TwitchLiveAnnouncerPlugin;
import it.ax3lt.tla.config.MessageConfiguration;
import it.ax3lt.tla.core.PluginContext;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public final class ForceCheckCommand implements CommandExecutor {

    private final TwitchLiveAnnouncerPlugin plugin;

    public ForceCheckCommand(TwitchLiveAnnouncerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("twitchliveannouncer.forceCheck")) {
            return true;
        }

        try {
            PluginContext.get().getStreamService().refreshStreams();
            sender.sendMessage(MessageConfiguration.getMessage("force_refresh_success"));
        } catch (IOException exception) {
            sender.sendMessage(MessageConfiguration.getMessage("force_refresh_error"));
            plugin.getLogger().warning("Force refresh failed: " + exception.getMessage());
        }
        return true;
    }
}
