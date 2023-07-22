package it.ax3lt.Commands.Link;

import it.ax3lt.Main.TLA;
import it.ax3lt.Utils.Configs.MessagesConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class ClearLinkCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("twitchliveannouncer.link.clear"))
            return true;

        if (args.length <= 2) {
            sender.sendMessage(Objects.requireNonNull(MessagesConfigUtils.getString("clear_link_usage")));
            return true;
        }

        String mcName = args[2];
        List<String> linkedUsers = TLA.getInstance().getConfig().getStringList("linked_users." + mcName);
        if (linkedUsers == null || linkedUsers.isEmpty()) {
            sender.sendMessage(Objects.requireNonNull(MessagesConfigUtils.getString("link-list-empty")));
            return true;
        }

        TLA.getInstance().getConfig().set("linked_users." + mcName, null);
        TLA.getInstance().saveConfig();
        TLA.getInstance().reloadConfig();
        sender.sendMessage(Objects.requireNonNull(MessagesConfigUtils.getString("link-cleared").replace("%player%", mcName)));
        return true;
    }
}
