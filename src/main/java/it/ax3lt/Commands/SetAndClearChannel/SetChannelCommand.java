package it.ax3lt.Commands.SetAndClearChannel;

import it.ax3lt.Main.TLA;
import it.ax3lt.Utils.Configs.MessagesConfigUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class SetChannelCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessagesConfigUtils.getString("mustbeplayer"));
            return true;
        }

        if (!sender.hasPermission("twitchliveannouncer.setchannel")) {
            sender.sendMessage(MessagesConfigUtils.getString("no_permission"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(MessagesConfigUtils.getString("setchannelusage"));
            return true;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();
        String channel = args[0];

        // Check if playerUUID and channel are already in the config
        List<String> linkedUsers = TLA.config.getStringList("linked_users." + playerUUID);

        if(!sender.hasPermission("twitchliveannouncer.link.multiple") && !linkedUsers.isEmpty()) {
            sender.sendMessage(MessagesConfigUtils.getString("already-have-a-link"));
            return true;
        }

        // Check if channel is in the list, if it isn't, add it
        if (!TLA.config.getStringList("channels").contains(channel)) {
            List<String> channels = TLA.config.getStringList("channels");
            channels.add(channel);
            TLA.config.set("channels", channels);
            try {
                TLA.config.save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Check if the link is already made
        if (linkedUsers != null && !linkedUsers.isEmpty()) {
            for (String s : linkedUsers) {
                if (s.equalsIgnoreCase(channel)) {
                    sender.sendMessage(Objects.requireNonNull(MessagesConfigUtils.getString("link-already-made")).replace(
                            "%channel%", channel
                    ));
                    return true;
                }
            }
        }

        linkedUsers.add(channel);
        TLA.config.set("linked_users." + playerUUID, linkedUsers);

        try {
            TLA.config.save();
            TLA.config.reload();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sender.sendMessage(MessagesConfigUtils.getString("channelset")
                .replace("%channel%", channel)
                .replace("%player%", player.getName()));



        return true;
    }
}
