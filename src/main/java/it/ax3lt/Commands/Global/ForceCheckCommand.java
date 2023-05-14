package it.ax3lt.Commands.Global;

import it.ax3lt.Main.TLA;
import it.ax3lt.Utils.Configs.MessagesConfigUtils;
import it.ax3lt.Utils.StreamUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

public class ForceCheckCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("twitchliveannouncer.forceCheck"))
            return true;
        try {
            StreamUtils.refresh();
            sender.sendMessage(Objects.requireNonNull(MessagesConfigUtils.getString("force_refresh_success"))
                .replace("%prefix%", Objects.requireNonNull(TLA.getInstance().getConfig().getString("prefix"))));
        } catch (IOException e) {
            sender.sendMessage(Objects.requireNonNull(MessagesConfigUtils.getString("force_refresh_error"))
                .replace("%prefix%", Objects.requireNonNull(TLA.getInstance().getConfig().getString("prefix"))));
            throw new RuntimeException(e);
        }
        return true;
    }
}
