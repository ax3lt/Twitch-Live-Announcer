package it.ax3lt.tla.command.stream;

import it.ax3lt.tla.TwitchLiveAnnouncerPlugin;
import it.ax3lt.tla.command.stream.channels.AddChannelCommand;
import it.ax3lt.tla.command.stream.channels.ListChannelCommand;
import it.ax3lt.tla.command.stream.channels.RemoveChannelCommand;
import it.ax3lt.tla.command.stream.global.ForceCheckCommand;
import it.ax3lt.tla.command.stream.global.ListStatusCommand;
import it.ax3lt.tla.command.stream.global.ReloadCommand;
import it.ax3lt.tla.command.stream.link.AddLinkCommand;
import it.ax3lt.tla.command.stream.link.ClearLinkCommand;
import it.ax3lt.tla.command.stream.link.ListLinkCommand;
import it.ax3lt.tla.command.stream.link.RemoveLinkCommand;
import it.ax3lt.tla.config.MessageConfiguration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class StreamCommandHandler implements CommandExecutor {

    private final ReloadCommand reloadCommand;
    private final ForceCheckCommand forceCheckCommand;
    private final ListStatusCommand listStatusCommand;
    private final AddLinkCommand addLinkCommand;
    private final ClearLinkCommand clearLinkCommand;
    private final RemoveLinkCommand removeLinkCommand;
    private final ListLinkCommand listLinkCommand;
    private final AddChannelCommand addChannelCommand;
    private final RemoveChannelCommand removeChannelCommand;
    private final ListChannelCommand listChannelCommand;

    public StreamCommandHandler(TwitchLiveAnnouncerPlugin plugin) {
        this.reloadCommand = new ReloadCommand(plugin);
        this.forceCheckCommand = new ForceCheckCommand(plugin);
        this.listStatusCommand = new ListStatusCommand();
        this.addLinkCommand = new AddLinkCommand(plugin);
        this.clearLinkCommand = new ClearLinkCommand(plugin);
        this.removeLinkCommand = new RemoveLinkCommand(plugin);
        this.listLinkCommand = new ListLinkCommand();
        this.addChannelCommand = new AddChannelCommand(plugin);
        this.removeChannelCommand = new RemoveChannelCommand(plugin);
        this.listChannelCommand = new ListChannelCommand();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(MessageConfiguration.getMessage("command_usage"));
            return true;
        }

        if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "link":
                    sender.sendMessage(MessageConfiguration.getMessage("link_command_usage"));
                    return true;
                case "channels":
                    sender.sendMessage(MessageConfiguration.getMessage("channels_command_usage"));
                    return true;
                case "reload":
                    return reloadCommand.onCommand(sender, command, label, args);
                case "forcecheck":
                    return forceCheckCommand.onCommand(sender, command, label, args);
                case "list":
                    return listStatusCommand.onCommand(sender, command, label, args);
                default:
                    return true;
            }
        }

        if (args[0].equalsIgnoreCase("link")) {
            String sub = args[1].toLowerCase();
            if (sub.equals("add")) {
                return addLinkCommand.onCommand(sender, command, label, args);
            }
            if (sub.equals("clear")) {
                return clearLinkCommand.onCommand(sender, command, label, args);
            }
            if (sub.equals("remove")) {
                return removeLinkCommand.onCommand(sender, command, label, args);
            }
            if (sub.equals("list") && args.length == 2) {
                return listLinkCommand.onCommand(sender, command, label, args);
            }
        }

        if (args[0].equalsIgnoreCase("channels")) {
            String sub = args[1].toLowerCase();
            if (sub.equals("add")) {
                return addChannelCommand.onCommand(sender, command, label, args);
            }
            if (sub.equals("remove")) {
                return removeChannelCommand.onCommand(sender, command, label, args);
            }
            if (sub.equals("list") && args.length == 2) {
                return listChannelCommand.onCommand(sender, command, label, args);
            }
        }

        return true;
    }
}
