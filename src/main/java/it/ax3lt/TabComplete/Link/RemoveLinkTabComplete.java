package it.ax3lt.TabComplete.Link;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import it.ax3lt.Main.TLA;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RemoveLinkTabComplete {

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // /stream link remove <mcname> <twitchname>
        //           0     1       2         3

        if (args.length == 3) {              // stream link remove
            // Get all the players in the config    //         ↑
            // Config format:
            // linked_users:
            //   <mcname>: <twitchname>

            List<String> players = new ArrayList<>();
            Section linkedUsers = TLA.config.getSection("linked_users");
            if (linkedUsers != null) {
                for (Object key : linkedUsers.getKeys()) {
                    players.add((String) key);
                }
            }

            if (players == null) {
                return Collections.singletonList("Empty list");
            }
            return players;
        } else if (args.length == 4) { // /stream link remove <mcname>
            //                                                    ↑
            List<String> players = TLA.config.getStringList("linked_users." + args[2]);

            if (players == null) {
                return Collections.singletonList("Empty list");
            }
            return players;
        }
        return null;
    }
}
