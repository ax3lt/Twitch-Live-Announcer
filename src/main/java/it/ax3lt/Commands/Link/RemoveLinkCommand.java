package it.ax3lt.Commands.Link;

import it.ax3lt.Main.TLA;
import it.ax3lt.Utils.Configs.MessagesConfigUtils;
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
            sender.sendMessage(Objects.requireNonNull(MessagesConfigUtils.getString("remove_link_usage")));
            return true;
        }

        String mcName = args[2];
        String twitchName = args[3];
        // Check if mcName and twitchName are in the config
        List<String> linkedUsers = TLA.getInstance().getConfig().getStringList("linked_users." + mcName);
        if (linkedUsers == null || linkedUsers.isEmpty()) {
            sender.sendMessage(Objects.requireNonNull(MessagesConfigUtils.getString("link-not-made"))
                    .replace("%channel%", twitchName)
                    .replace("%player%", mcName));
            return true;
        }

        for (String s : linkedUsers) {
            if (s.equalsIgnoreCase(twitchName)) {
                linkedUsers.remove(s);
                TLA.getInstance().getConfig().set("linked_users." + mcName, linkedUsers);
                TLA.getInstance().saveConfig();
                TLA.getInstance().reloadConfig();
                sender.sendMessage(Objects.requireNonNull(MessagesConfigUtils.getString("link-removed"))
                        .replace("%channel%", twitchName)
                        .replace("%player%", mcName));
                return true;
            }
        }
        return true;
    }
}