package it.ax3lt.TabComplete;

import it.ax3lt.Main.TLA;
import it.ax3lt.TabComplete.Link.RemoveLinkTabComplete;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class StreamCommandTabHandler implements TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // reload
        // forceCheck
        // list
        // link
        //      add <mcname> <twitchname>
        //      remove <mcname> <twitchname>
        //      list
        // channels
        //      add <twitchChannel>
        //      remove <twitchChannel>
        //      list

        if (args.length == 1) { // /stream
            return List.of("reload", "forceCheck", "list", "link", "channels");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("link")) { // /stream link
            return List.of("add", "remove", "clear", "list");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("channels")) { // /stream channels
            return List.of("add", "remove", "list");
        }


        if (args.length >= 3 && args[0].equalsIgnoreCase("link") && args[1].equalsIgnoreCase("add")) { // /stream link add
            if(args.length == 3)
                return List.of("<mcname>");
            if(args.length == 4)
                return List.of("<twitchname>");
        }
        if(args.length >= 3 && args[0].equalsIgnoreCase("link") && args[1].equalsIgnoreCase("clear")) {
            return List.of("<mcname>");
        }
        if (args.length >= 3 && args[0].equalsIgnoreCase("link") && args[1].equalsIgnoreCase("remove")) {
            return new RemoveLinkTabComplete().onTabComplete(sender, command, label, args);
        }


        if (args.length == 3 && args[0].equalsIgnoreCase("channels") && args[1].equalsIgnoreCase("add")) { // /stream channels add
            return List.of("<twitchChannel>");
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("channels") && args[1].equalsIgnoreCase("remove")) { // /stream channels remove (list of channels)
            return TLA.getInstance().getConfig().getStringList("channels");
        }

        return null;
    }
}
