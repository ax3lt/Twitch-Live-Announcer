package it.ax3lt.PlaceHolderApiExpansion;

import it.ax3lt.Handlers.AnnouncerHandler;
import it.ax3lt.Main.StreamAnnouncer;
import it.ax3lt.Utils.ConfigUtils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlaceHolderManager extends PlaceholderExpansion {
    private final Plugin plugin;


    @Override
    public String onRequest(OfflinePlayer player, String params) { // live_<mcname>

        if (params.startsWith("status")) {
            if (player == null) {
                return "";
            }

            String username = player.getName();

            // Check in config file if the user is linked
            ConfigurationSection linked_users = StreamAnnouncer.getInstance().getConfig().getConfigurationSection("linked_users");
            if (linked_users != null) {
                if (linked_users.contains(username)) {
                    // Check if the user is online
                    List<String> streams = linked_users.getStringList(username);
                    if (streams != null && !streams.isEmpty()) {
                        for (String s : streams) {
                            if (AnnouncerHandler.streams.get(s) != null) {
                                return ConfigUtils.getConfigString("placeholders.live");
                            } else {
                                return ConfigUtils.getConfigString("placeholders.offline");
                            }
                        }
                    }
                }
            }
            else {
                return "";
            }

        }


        if (params.startsWith("live_")) {
            String[] split = params.split("_");
            if (split.length == 2) {
                String username = split[1];
                // Check in config file if the user is linked
                ConfigurationSection linked_users = StreamAnnouncer.getInstance().getConfig().getConfigurationSection("linked_users");
                if (linked_users != null) {
                    if (linked_users.contains(username)) {
                        // Check if the user is online
                        List<String> streams = linked_users.getStringList(username);
                        if (streams != null && !streams.isEmpty()) {
                            for (String s : streams) {
                                if (AnnouncerHandler.streams.get(s) != null) {
                                    return "true";
                                } else {
                                    return "false";
                                }
                            }
                        }

                    }
                }
            }
        }
        if (params.startsWith("status_string_") && StreamAnnouncer.getInstance().

                getConfig().

                getBoolean("placeholders.enabled")) {
            String[] split = params.split("_");
            if (split.length == 3) {
                String username = split[2];
                // Check in config file if the user is linked
                ConfigurationSection linked_users = StreamAnnouncer.getInstance().getConfig().getConfigurationSection("linked_users");
                if (linked_users != null) {
                    if (linked_users.contains(username)) {
                        // Check if the user is online
                        List<String> streams = linked_users.getStringList(username);
                        if (streams != null && !streams.isEmpty()) {
                            for (String s : streams) {
                                if (AnnouncerHandler.streams.get(s) != null) {
                                    return ConfigUtils.getConfigString("placeholders.live");
                                } else {
                                    return ConfigUtils.getConfigString("placeholders.offline");
                                }
                            }
                        }

                    }
                }
            }

        }
        return null; // Placeholder is unknown by the Expansion
    }


    @Override
    public @NotNull String getIdentifier() {
        return "tla";
    }

    @Override
    public @NotNull String getAuthor() {
        return "ax3lt";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.3";
    }

    public PlaceHolderManager(Plugin p) {
        this.plugin = p;
    }
}
