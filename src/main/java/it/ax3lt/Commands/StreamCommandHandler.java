package it.ax3lt.Commands;

import it.ax3lt.Commands.Channels.AddChannelCommand;
import it.ax3lt.Commands.Channels.ListChannelCommand;
import it.ax3lt.Commands.Channels.RemoveChannelCommand;
import it.ax3lt.Commands.Global.ForceCheckCommand;
import it.ax3lt.Commands.Global.ListStatus;
import it.ax3lt.Commands.Global.ReloadCommand;
import it.ax3lt.Commands.Link.AddLinkCommand;
import it.ax3lt.Commands.Link.ClearLinkCommand;
import it.ax3lt.Commands.Link.ListLinkCommand;
import it.ax3lt.Commands.Link.RemoveLinkCommand;
import it.ax3lt.Utils.Configs.MessagesConfigUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class StreamCommandHandler implements CommandExecutor {
    // /stream
    // reload
    // forceUpdate
    // list
    // link
    //      add
    //      remove
    //      clear
    //      list
    // channels
    //      add
    //      remove
    //      list

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Help messages
        if (args.length == 0)
            sender.sendMessage(MessagesConfigUtils.getString("command_usage"));
        else if (args.length == 1 && args[0].equalsIgnoreCase("link"))
            sender.sendMessage(MessagesConfigUtils.getString("link_command_usage"));
        else if (args.length == 1 && args[0].equalsIgnoreCase("channels"))
            sender.sendMessage(MessagesConfigUtils.getString("channels_command_usage"));

            // Reload
        else if (args.length == 1 && args[0].equalsIgnoreCase("reload"))
            new ReloadCommand().onCommand(sender, command, label, args);

        // Force update
        else if(args.length == 1 && args[0].equalsIgnoreCase("forceCheck"))
            new ForceCheckCommand().onCommand(sender, command, label, args);

        // List
        else if (args.length == 1 && args[0].equalsIgnoreCase("list"))
            new ListStatus().onCommand(sender, command, label, args);

            // Link
        else if (args.length >= 2 && args[0].equalsIgnoreCase("link") && args[1].equalsIgnoreCase("add"))
            new AddLinkCommand().onCommand(sender, command, label, args);
        else if (args.length >= 2 && args[0].equalsIgnoreCase("link") && args[1].equalsIgnoreCase("clear"))
            new ClearLinkCommand().onCommand(sender, command, label, args);
        else if (args.length >= 2 && args[0].equalsIgnoreCase("link") && args[1].equalsIgnoreCase("remove"))
            new RemoveLinkCommand().onCommand(sender, command, label, args);
        else if (args.length == 2 && args[0].equalsIgnoreCase("link") && args[1].equalsIgnoreCase("list"))
            new ListLinkCommand().onCommand(sender, command, label, args);

            // Channels
        else if (args.length >= 2 && args[0].equalsIgnoreCase("channels") && args[1].equalsIgnoreCase("add"))
            new AddChannelCommand().onCommand(sender, command, label, args);
        else if (args.length >= 2 && args[0].equalsIgnoreCase("channels") && args[1].equalsIgnoreCase("remove"))
            new RemoveChannelCommand().onCommand(sender, command, label, args);
        else if (args.length == 2 && args[0].equalsIgnoreCase("channels") && args[1].equalsIgnoreCase("list"))
            new ListChannelCommand().onCommand(sender, command, label, args);

        return true;
    }
}


