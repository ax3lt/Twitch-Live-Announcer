package it.ax3lt.Commands.Stream.Global;

import it.ax3lt.Main.TLA;
import it.ax3lt.Utils.Configs.MessagesConfigUtils;
import it.ax3lt.Utils.StreamUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

public class ReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("twitchliveannouncer.reload"))
            return true;
        try {
            TLA.config.reload();
            TLA.messages.reload();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sender.sendMessage(Objects.requireNonNull(MessagesConfigUtils.getString("reload_config"))
                .replace("%prefix%", Objects.requireNonNull(TLA.config.getString("prefix"))));

        if (StreamUtils.credentialsChanged()) {
            // New client_id/client_secret, fetch a new token off the main thread before restarting the tasks
            Bukkit.getScheduler().runTaskAsynchronously(TLA.getInstance(), () -> {
                try {
                    StreamUtils.configureParameters();
                } catch (IOException e) {
                    sender.sendMessage(Objects.requireNonNull(MessagesConfigUtils.getString("parameters_error"))
                            .replace("%prefix%", Objects.requireNonNull(TLA.config.getString("prefix")))
                            .replace("%message%", e.getMessage()));
                }
                TLA.getInstance().restartTasks();
            });
        } else {
            TLA.getInstance().restartTasks();
        }
        return true;
    }
}
