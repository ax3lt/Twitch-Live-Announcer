package it.ax3lt.Utils.Configs;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import it.ax3lt.Main.TLA;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class ConfigUtils {
    public static String getConfigString(String name) {
        return ChatColor.translateAlternateColorCodes('&', TLA.config.getString(name)
                .replace("%prefix%", Objects.requireNonNull(TLA.config.getString("prefix"))));
    }

    public static List<String> getLinkedUserStringList(String mcName) {
        mcName = mcName.replaceAll("\\.", "%%DOT%%");
        return TLA.config.getStringList("linked_users." + mcName);
    }

    public static void setLinkedUserStringList(String mcName, List<String> data) {
        mcName = mcName.replaceAll("\\.", "%%DOT%%");
        TLA.config.set("linked_users." + mcName, data);
        try {
            TLA.config.save();
            TLA.config.reload();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encodeUsername(String s) {
        return s.replaceAll("\\.", "%%DOT%%");
    }

    public static String decodeUsername(String s) {
        return s.replaceAll("%%DOT%%", ".");
    }

}