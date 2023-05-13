package it.ax3lt.Commands.Global;

import it.ax3lt.Main.TLA;
import it.ax3lt.Utils.Configs.MessagesConfigUtils;
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
        TLA.getInstance().reloadConfig();
        MessagesConfigUtils.reload();
        sender.sendMessage(Objects.requireNonNull(MessagesConfigUtils.getString("reload_config"))
                .replace("%prefix%", Objects.requireNonNull(TLA.getInstance().getConfig().getString("prefix"))));
        return true;
    }
}
