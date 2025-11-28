package it.ax3lt.tla.command.stream.global;

import it.ax3lt.tla.TwitchLiveAnnouncerPlugin;
import it.ax3lt.tla.config.MessageConfiguration;
import it.ax3lt.tla.core.PluginContext;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public final class ReloadCommand implements CommandExecutor {

    private final TwitchLiveAnnouncerPlugin plugin;

    public ReloadCommand(TwitchLiveAnnouncerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("twitchliveannouncer.reload")) {
            return true;
        }

        try {
            PluginContext.get().getConfiguration().reload();
            PluginContext.get().getMessages().reload();
            PluginContext.get().getConfiguration().save();
            PluginContext.get().getMessages().save();
        } catch (IOException exception) {
            plugin.getLogger().severe("Failed to reload configuration: " + exception.getMessage());
            return true;
        }

        sender.sendMessage(MessageConfiguration.getMessage("reload_config"));
        return true;
    }
}
