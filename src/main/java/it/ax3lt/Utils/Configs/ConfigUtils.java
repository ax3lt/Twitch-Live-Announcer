package it.ax3lt.Utils.Configs;

import it.ax3lt.Main.TLA;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ConfigUtils { // Test
    public static String getConfigString(String name) {
        return ChatColor.translateAlternateColorCodes('&', TLA.config.getString(name)
                .replace("%prefix%", Objects.requireNonNull(TLA.config.getString("prefix"))));
    }
}