package it.ax3lt.Commands.Channels;

import it.ax3lt.Main.TLA;
import it.ax3lt.Utils.Configs.ConfigUtils;
import it.ax3lt.Utils.Configs.MessagesConfigUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class AddChannelCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // /stream channels add <channel>
        //            0      1      2
        if (!sender.hasPermission("twitchliveannouncer.channels.add"))
            return true;

        if (args.length < 3) {
            sender.sendMessage(Objects.requireNonNull(MessagesConfigUtils.getString("add_channel_usage"))
                    .replace("%prefix%", Objects.requireNonNull(ConfigUtils.getConfigString("prefix"))));
            return true;
        }


        List<String> channels = TLA.config.getStringList("channels");
        if (channels.contains(args[2]))
            sender.sendMessage(MessagesConfigUtils.getString("channel-already-added").replace("%channel%", args[2]));
        else {
            channels.add(args[2]);
            sender.sendMessage(MessagesConfigUtils.getString("channel-added").replace("%channel%", args[2]));
            TLA.config.set("channels", channels);
        }
        try {
            TLA.config.save();
            TLA.config.reload();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
