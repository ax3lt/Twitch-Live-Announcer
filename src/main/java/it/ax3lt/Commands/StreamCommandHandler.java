package it.ax3lt.Commands;

import it.ax3lt.BungeeManager.MessageSender;
import it.ax3lt.Commands.Link.AddCommand;
import it.ax3lt.Commands.Global.ReloadCommand;
import it.ax3lt.Commands.Link.RemoveCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class StreamCommandHandler implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            new ReloadCommand().onCommand(sender, command, label, args);
        } else if (args.length > 0 && args[0].equalsIgnoreCase("add")) {
            new AddCommand().onCommand(sender, command, label, args);
        } else if (args.length >= 1 && args[0].equalsIgnoreCase("remove")) {
            new RemoveCommand().onCommand(sender, command, label, args);
        }
        return true;
    }
}
