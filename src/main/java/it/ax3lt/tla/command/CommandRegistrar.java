package it.ax3lt.tla.command;

import it.ax3lt.tla.TwitchLiveAnnouncerPlugin;
import it.ax3lt.tla.command.setchannel.ClearChannelCommand;
import it.ax3lt.tla.command.setchannel.SetChannelCommand;
import it.ax3lt.tla.command.stream.StreamCommandHandler;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;

import java.util.Objects;

public final class CommandRegistrar {

    private final TwitchLiveAnnouncerPlugin plugin;

    public CommandRegistrar(TwitchLiveAnnouncerPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerCommands() {
        register("stream", new StreamCommandHandler(plugin));
        register("setchannel", new SetChannelCommand(plugin));
        register("clearchannel", new ClearChannelCommand(plugin));
    }

    private void register(String commandName, CommandExecutor executor) {
        Objects.requireNonNull(plugin.getCommand(commandName)).setExecutor(executor);
    }

    public void registerTabCompleter(TabCompleter tabCompleter) {
        Objects.requireNonNull(plugin.getCommand("stream")).setTabCompleter(tabCompleter);
    }
}
