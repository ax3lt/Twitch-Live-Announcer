package it.ax3lt.Commands.Link;

import it.ax3lt.Main.TLA;
import it.ax3lt.Utils.Configs.ConfigUtils;
import it.ax3lt.Utils.Configs.MessagesConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
        UUID playerUUID = Bukkit.getOfflinePlayer(mcName).getUniqueId();
        List<String> linkedUsers = TLA.config.getStringList("linked_users." + playerUUID.toString());
        if (linkedUsers == null || linkedUsers.isEmpty()) {
            sender.sendMessage(Objects.requireNonNull(MessagesConfigUtils.getString("link-list-empty")));
            return true;
        }

        TLA.config.set("linked_users." + playerUUID.toString(), null);
        try {
            TLA.config.save();
            TLA.config.reload();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sender.sendMessage(Objects.requireNonNull(MessagesConfigUtils.getString("link-cleared").replace("%player%", mcName)));
        return true;
    }
}
