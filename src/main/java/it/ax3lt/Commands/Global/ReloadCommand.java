package it.ax3lt.Commands.Global;

import it.ax3lt.Main.StreamAnnouncer;
import it.ax3lt.Utils.ConfigUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("twitchliveannouncer.reload"))
            return true;
        StreamAnnouncer.getInstance().reloadConfig();
        sender.sendMessage(Objects.requireNonNull(ConfigUtils.getConfigString("reload_config"))
                .replace("%prefix", Objects.requireNonNull(StreamAnnouncer.getInstance().getConfig().getString("prefix"))));
        return true;
    }
}
