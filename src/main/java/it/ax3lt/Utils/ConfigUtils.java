package it.ax3lt.Utils;

import it.ax3lt.Main.StreamAnnouncer;
import org.bukkit.ChatColor;

import java.util.Objects;

public class ConfigUtils {


    public static String getConfigString(String name) {
        return ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(StreamAnnouncer.getInstance().getConfig().getString(name)
                .replace("%prefix%", Objects.requireNonNull(StreamAnnouncer.getInstance().getConfig().getString("prefix")))));
    }
}
