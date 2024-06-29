package it.ax3lt.Commands.SetAndClearChannel;

import it.ax3lt.Main.TLA;
import it.ax3lt.Utils.Configs.MessagesConfigUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class ClearChannelCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessagesConfigUtils.getString("mustbeplayer"));
            return true;
        }

        if (!sender.hasPermission("twitchliveannouncer.clearchannel")) {
            sender.sendMessage(MessagesConfigUtils.getString("no_permission"));
            return true;
        }

        if(args.length != 0) {
            sender.sendMessage(MessagesConfigUtils.getString("clearchannelusage"));
            return true;
        }

        UUID playerUUID = ((Player) sender).getUniqueId();
        List<String> linkedUsers = TLA.config.getStringList("linked_users." + playerUUID);
        if (linkedUsers == null || linkedUsers.isEmpty()) {
            sender.sendMessage(MessagesConfigUtils.getString("nolinkedchannel"));
            return true;
        }


        // Remove the channel from the list
        List<String> channels = TLA.config.getStringList("channels");
        for (String s : linkedUsers) {
            channels.remove(s);
        }

        TLA.config.set("channels", channels);
        TLA.config.set("linked_users." + playerUUID, null);
        try {
            TLA.config.save();
            TLA.config.reload();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        sender.sendMessage(MessagesConfigUtils.getString("channelcleared"));

        return true;
    }
}
