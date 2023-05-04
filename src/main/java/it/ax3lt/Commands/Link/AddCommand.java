package it.ax3lt.Commands.Link;

import it.ax3lt.Main.StreamAnnouncer;
import it.ax3lt.Utils.ConfigUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class AddCommand implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("twitchliveannouncer.link.add"))
            return true;


        if (args.length < 3) {
            sender.sendMessage(Objects.requireNonNull(ConfigUtils.getConfigString("add_channel_usage"))
                    .replace("%prefix%", Objects.requireNonNull(ConfigUtils.getConfigString("prefix"))));
            return true;
        }


        String mcName = args[1];
        String twitchName = args[2];
        // Check if mcName and twitchName are already in the config
        List<String> linkedUsers = StreamAnnouncer.getInstance().getConfig().getStringList("linked_users." + mcName);

        if (linkedUsers != null && !linkedUsers.isEmpty()) {
            for (String s : linkedUsers) {
                if (s.equalsIgnoreCase(twitchName)) {
                    sender.sendMessage(Objects.requireNonNull(ConfigUtils.getConfigString("link-already-made")).replace(
                            "%channel%", twitchName
                    ));
                    return true;
                }
            }
        }


        linkedUsers.add(twitchName);
        StreamAnnouncer.getInstance().getConfig().set("linked_users." + mcName, linkedUsers);
        StreamAnnouncer.getInstance().saveConfig();
        StreamAnnouncer.getInstance().reloadConfig();
        sender.sendMessage(Objects.requireNonNull(ConfigUtils.getConfigString("link-made"))
                .replace("%channel%", twitchName)
                .replace("%player%", mcName));
        return true;
    }
}
