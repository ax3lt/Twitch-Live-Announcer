package it.ax3lt.Commands.Link;

import it.ax3lt.Main.StreamAnnouncer;
import it.ax3lt.Utils.ConfigUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class RemoveLinkCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!sender.hasPermission("twitchliveannouncer.link.remove"))
            return true;

        if (args.length <= 3) {
            sender.sendMessage(Objects.requireNonNull(ConfigUtils.getConfigString("remove_link_usage")));
            return true;
        }

        String mcName = args[2];
        String twitchName = args[3];
        // Check if mcName and twitchName are in the config
        List<String> linkedUsers = StreamAnnouncer.getInstance().getConfig().getStringList("linked_users." + mcName);
        if (linkedUsers == null || linkedUsers.isEmpty()) {
            sender.sendMessage(Objects.requireNonNull(ConfigUtils.getConfigString("link-not-made"))
                    .replace("%channel%", twitchName)
                    .replace("%player%", mcName));
            return true;
        }

        for (String s : linkedUsers) {
            if (s.equalsIgnoreCase(twitchName)) {
                linkedUsers.remove(s);
                StreamAnnouncer.getInstance().getConfig().set("linked_users." + mcName, linkedUsers);
                StreamAnnouncer.getInstance().saveConfig();
                StreamAnnouncer.getInstance().reloadConfig();
                sender.sendMessage(Objects.requireNonNull(ConfigUtils.getConfigString("link-removed"))
                        .replace("%channel%", twitchName)
                        .replace("%player%", mcName));
                return true;
            }
        }
        return true;
    }
}
