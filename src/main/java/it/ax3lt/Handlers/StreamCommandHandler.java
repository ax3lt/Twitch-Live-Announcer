package it.ax3lt.Handlers;

import it.ax3lt.Main.StreamAnnouncer;
import it.ax3lt.Utils.ConfigUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StreamCommandHandler implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.hasPermission("twitchliveannouncer.reload") && args.length == 1 &&
                args[0].equalsIgnoreCase("reload")) {
            StreamAnnouncer.getInstance().reloadConfig();
            sender.sendMessage(Objects.requireNonNull(ConfigUtils.getConfigString("reload_config"))
                    .replace("%prefix", Objects.requireNonNull(StreamAnnouncer.getInstance().getConfig().getString("prefix"))));
        }


        if(args.length >= 1 && args[0].equalsIgnoreCase("add") && sender.hasPermission("twitchliveannouncer.link.add"))
        {
            if(args.length < 2)
            {
                sender.sendMessage(Objects.requireNonNull(ConfigUtils.getConfigString("add_channel_usage"))
                        .replace("%prefix%", Objects.requireNonNull(ConfigUtils.getConfigString("prefix"))));
                return true;
            }

            String mcName = args[1];
            String twitchName = args[2];
            // Check if mcName and twitchName are already in the config
            List<String> linkedUsers = StreamAnnouncer.getInstance().getConfig().getStringList("linked_users." + mcName);

            if(linkedUsers != null && !linkedUsers.isEmpty())
            {
                for(String s : linkedUsers)
                {
                    if(s.equalsIgnoreCase(twitchName))
                    {
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
        }

        if(args.length >= 1 && args[0].equalsIgnoreCase("remove") && sender.hasPermission("twitchliveannouncer.link.add")) {
            if (args.length <= 2) {
                sender.sendMessage(Objects.requireNonNull(ConfigUtils.getConfigString("remove_channel_usage")));
                return true;
            }

            String mcName = args[1];
            String twitchName = args[2];
            // Check if mcName and twitchName are in the config
            List<String> linkedUsers = StreamAnnouncer.getInstance().getConfig().getStringList("linked_users." + mcName);
            if (linkedUsers == null || linkedUsers.isEmpty()) {
                sender.sendMessage(Objects.requireNonNull(ConfigUtils.getConfigString("link-not-made"))
                        .replace("%channel%", twitchName)
                        .replace("%player%", mcName));
                return true;
            }

            for (String s : linkedUsers) {
                if (s.equalsIgnoreCase(twitchName)) {
                    linkedUsers.remove(s);
                    StreamAnnouncer.getInstance().getConfig().set("linked_users." + mcName, linkedUsers);
                    StreamAnnouncer.getInstance().saveConfig();
                    StreamAnnouncer.getInstance().reloadConfig();
                    sender.sendMessage(Objects.requireNonNull(ConfigUtils.getConfigString("link-removed"))
                            .replace("%channel%", twitchName)
                            .replace("%player%", mcName));
                    return true;
                }
            }
            return true;
        }
        return true;
    }
}
