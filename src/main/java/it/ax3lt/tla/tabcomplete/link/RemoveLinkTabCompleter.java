package it.ax3lt.tla.tabcomplete.link;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import it.ax3lt.tla.core.PluginContext;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class RemoveLinkTabCompleter {

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 3) {
            List<String> players = new ArrayList<>();
            Section linkedUsers = PluginContext.get().getConfiguration().getSection("linked_users");
            if (linkedUsers != null) {
                for (Object key : linkedUsers.getKeys()) {
                    UUID uuid = UUID.fromString((String) key);
                    String name = Bukkit.getOfflinePlayer(uuid).getName();
                    players.add(name != null ? name : "Unknown");
                }
            }
            return players.isEmpty() ? Collections.singletonList("Empty list") : players;
        }

        if (args.length == 4) {
            UUID uuid = Bukkit.getOfflinePlayer(args[2]).getUniqueId();
            List<String> channels = PluginContext.get().getConfiguration().getStringList("linked_users." + uuid);
            return channels == null || channels.isEmpty() ? Collections.singletonList("Empty list") : channels;
        }

        return Collections.emptyList();
    }
}
