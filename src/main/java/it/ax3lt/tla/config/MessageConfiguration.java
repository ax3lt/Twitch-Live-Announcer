package it.ax3lt.tla.config;

import it.ax3lt.tla.core.PluginContext;
import org.bukkit.ChatColor;

public final class MessageConfiguration {

    private MessageConfiguration() {
    }

    public static String getMessage(String path) {
        String message = PluginContext.get().getMessages().getString(path);
        if (message == null) {
            return "";
        }

        String prefix = ConfigurationFormatter.getConfigString("prefix");
        message = message.replace("%prefix%", prefix);
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
