package it.ax3lt.Commands.Stream.Link;

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
import java.util.stream.Collectors;

public class AddLinkCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("twitchliveannouncer.link.add"))
            return true;


        if (args.length < 4) {
            sender.sendMessage(Objects.requireNonNull(MessagesConfigUtils.getString("add_link_usage"))
                    .replace("%prefix%", Objects.requireNonNull(ConfigUtils.getConfigString("prefix"))));
            return true;
        }


        String mcName = args[2];
        UUID playerUUID = Bukkit.getOfflinePlayer(mcName).getUniqueId();
        String twitchName = args[3];

        // Check if mcName and twitchName are already in the config
//        List<String> linkedUsers = TLA.config.getStringList("linked_users." + mcName);
        List<String> linkedUsers = TLA.config.getStringList("linked_users." + playerUUID);


        // Check if the player already has a link (twitchliveannouncer.link.multiple can bypass)
        if (!sender.hasPermission("twitchliveannouncer.link.multiple") && !linkedUsers.isEmpty()) {
            sender.sendMessage(Objects.requireNonNull(MessagesConfigUtils.getString("already-have-a-link")));
            return true;
        }

        List<String> channels = TLA.config.getStringList("channels");

        String twitchNameLowerCase = twitchName.toLowerCase();

        List<String> lowerCaseChannels = channels.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        if (!lowerCaseChannels.contains(twitchNameLowerCase)) {
            // Add the channel in its original case
            channels.add(twitchName);
            TLA.config.set("channels", channels);
            try {
                TLA.config.save();
                TLA.config.reload();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


        // Check if the link is already made
        if (linkedUsers != null && !linkedUsers.isEmpty()) {
            for (String s : linkedUsers) {
                if (s.equalsIgnoreCase(twitchName)) {
                    sender.sendMessage(Objects.requireNonNull(MessagesConfigUtils.getString("link-already-made")).replace(
                            "%channel%", twitchName
                    ));
                    return true;
                }
            }
        }


        linkedUsers.add(twitchName);
        TLA.config.set("linked_users." + playerUUID, linkedUsers);


        try {
            TLA.config.save();
            TLA.config.reload();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sender.sendMessage(Objects.requireNonNull(MessagesConfigUtils.getString("link-made"))
                .replace("%channel%", twitchName)
                .replace("%player%", mcName));
        return true;
    }
}
