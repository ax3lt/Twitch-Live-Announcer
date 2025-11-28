package it.ax3lt.tla.config;

import it.ax3lt.tla.core.PluginContext;
import org.bukkit.ChatColor;

public final class ConfigurationFormatter {

    private ConfigurationFormatter() {
    }

    public static String getConfigString(String path) {
        String rawValue = PluginContext.get().getConfiguration().getString(path);
        if (rawValue == null) {
            return "";
        }

        String prefix = PluginContext.get().getConfiguration().getString("prefix");
        if (prefix != null) {
            rawValue = rawValue.replace("%prefix%", prefix);
        }
        return ChatColor.translateAlternateColorCodes('&', rawValue);
    }
}
