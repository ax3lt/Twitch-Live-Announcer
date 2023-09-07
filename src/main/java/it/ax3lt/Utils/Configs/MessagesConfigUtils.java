package it.ax3lt.Utils.Configs;

import com.google.common.io.ByteStreams;
import it.ax3lt.Main.TLA;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.Objects;

public class MessagesConfigUtils {

    public static String getString(String name) {
        return ChatColor.translateAlternateColorCodes('&',
                Objects.requireNonNull(Objects.requireNonNull(TLA.messages.getString(name))
                .replace("%prefix%", Objects.requireNonNull(TLA.config.getString("prefix")))));
    }
}
