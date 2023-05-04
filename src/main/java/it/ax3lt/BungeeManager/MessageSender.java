package it.ax3lt.BungeeManager;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import it.ax3lt.Main.StreamAnnouncer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MessageSender {
    public static void sendBungeeMessage(String message) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Message");
        out.writeUTF("ALL");
        out.writeUTF(message);
        Bukkit.getServer().sendPluginMessage(StreamAnnouncer.getInstance(), "BungeeCord", out.toByteArray());
    }

    public static void sendRawBungeeMessage(String message) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("MessageRaw");
        out.writeUTF("ALL");
        out.writeUTF(message);
        Bukkit.getServer().sendPluginMessage(StreamAnnouncer.getInstance(), "BungeeCord", out.toByteArray());
    }
}
