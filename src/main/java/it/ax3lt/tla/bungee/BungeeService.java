package it.ax3lt.tla.bungee;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import it.ax3lt.tla.TwitchLiveAnnouncerPlugin;
import it.ax3lt.tla.core.PluginContext;
import org.bukkit.Bukkit;

public final class BungeeService {

    private final TwitchLiveAnnouncerPlugin plugin;

    public BungeeService(TwitchLiveAnnouncerPlugin plugin) {
        this.plugin = plugin;
    }

    public void register() {
        if (!PluginContext.get().getConfiguration().getBoolean("bungee.enabled")) {
            PluginContext.get().setBungeeMode(false);
            return;
        }

        PluginContext.get().setBungeeMode(true);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", new BungeeMessageListener());
    }

    public void unregister() {
        plugin.getServer().getMessenger().unregisterOutgoingPluginChannel(plugin);
        plugin.getServer().getMessenger().unregisterIncomingPluginChannel(plugin);
    }

    public void sendMessage(String message) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("Message");
        output.writeUTF("ALL");
        output.writeUTF(message);
        Bukkit.getServer().sendPluginMessage(plugin, "BungeeCord", output.toByteArray());
    }

    public void sendRawMessage(String message) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("MessageRaw");
        output.writeUTF("ALL");
        output.writeUTF(message);
        Bukkit.getServer().sendPluginMessage(plugin, "BungeeCord", output.toByteArray());
    }
}
