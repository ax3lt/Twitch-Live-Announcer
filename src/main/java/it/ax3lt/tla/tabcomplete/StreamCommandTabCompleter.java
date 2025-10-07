package it.ax3lt.tla.tabcomplete;

import it.ax3lt.tla.core.PluginContext;
import it.ax3lt.tla.tabcomplete.link.RemoveLinkTabCompleter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public final class StreamCommandTabCompleter implements TabCompleter {

    private final RemoveLinkTabCompleter removeLinkTabCompleter = new RemoveLinkTabCompleter();

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("reload", "forceCheck", "list", "link", "channels");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("link")) {
            return List.of("add", "remove", "clear", "list");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("channels")) {
            return List.of("add", "remove", "list");
        }

        if (args.length >= 3 && args[0].equalsIgnoreCase("link") && args[1].equalsIgnoreCase("add") && sender.hasPermission("twitchliveannouncer.link.add")) {
            if (args.length == 3) {
                return List.of("<mcname>");
            }
            if (args.length == 4) {
                return List.of("<twitchname>");
            }
        }

        if (args.length >= 3 && args[0].equalsIgnoreCase("link") && args[1].equalsIgnoreCase("clear") && sender.hasPermission("twitchliveannouncer.link.clear")) {
            return List.of("<mcname>");
        }

        if (args.length >= 3 && args[0].equalsIgnoreCase("link") && args[1].equalsIgnoreCase("remove") && sender.hasPermission("twitchliveannouncer.link.remove")) {
            return removeLinkTabCompleter.onTabComplete(sender, command, label, args);
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("channels") && args[1].equalsIgnoreCase("add") && sender.hasPermission("twitchliveannouncer.channels.add")) {
            return List.of("<twitchChannel>");
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("channels") && args[1].equalsIgnoreCase("remove") && sender.hasPermission("twitchliveannouncer.channels.remove")) {
            return PluginContext.get().getConfiguration().getStringList("channels");
        }

        return Collections.emptyList();
    }
}
