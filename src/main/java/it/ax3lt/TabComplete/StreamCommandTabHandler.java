package it.ax3lt.TabComplete;

import it.ax3lt.Main.StreamAnnouncer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class StreamCommandTabHandler implements TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // /stream reload
        // /stream add <mcname> <twitchname>
        // /stream remove <mcname> <twitchname>
        if (args.length == 1) {
            return List.of("reload", "add", "remove");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            // Get all the players in the config
            // Config format:
            // linked_users:
            //   <mcname>: <twitchname>

            List<String> players = new ArrayList<>();
            ConfigurationSection linkedUsers = StreamAnnouncer.getInstance().getConfig().getConfigurationSection("linked_users");
            if (linkedUsers != null) {
                for (String key : linkedUsers.getKeys(false)) {
                    players.add(key);
                }
            }

            if (players == null) {
                return Collections.singletonList("Empty list");
            }
            return players;
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("remove")) {
            // Get all the players in the config
            // Config format:
            // linked_users:
            //   <mcname>: <twitchname>
            List<String> players = StreamAnnouncer.getInstance().getConfig().getStringList("linked_users." + args[1]);

            if (players == null) {
                return Collections.singletonList("Empty list");
            }
            return players;
        }
        if(args.length == 2 && args[0].equalsIgnoreCase("add")) {
            return Collections.singletonList("<mcname>");
        }
        if(args.length == 3 && args[0].equalsIgnoreCase("add")) {
            return Collections.singletonList("<twitchname>");
        }


        return null;
    }
}
