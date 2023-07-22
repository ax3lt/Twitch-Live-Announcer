package it.ax3lt.Utils.Configs;

import com.google.common.io.ByteStreams;
import it.ax3lt.Main.TLA;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.Objects;

public class MessagesConfigUtils {
    public static YamlConfiguration config;

    public static void setup() {
        File messagesFile = new File(TLA.getInstance().getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            try (InputStream in = TLA.getInstance().getResource("messages.yml");
                 OutputStream out = new FileOutputStream(messagesFile)) {
                ByteStreams.copy(in, out);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(messagesFile);
        config.options().copyDefaults(true);
    }

    public static void save() {
        try {
            config.save(new File(TLA.getInstance().getDataFolder(), "messages.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getString(String name) {
        return ChatColor.translateAlternateColorCodes('&',
                Objects.requireNonNull(Objects.requireNonNull(config.getString(name))
                .replace("%prefix%", Objects.requireNonNull(TLA.getInstance().getConfig().getString("prefix")))));
    }

    public static void reload() {
        File usersFile = new File(TLA.getInstance().getDataFolder(), "messages.yml");
        if (!usersFile.exists()) {
            try (InputStream in = TLA.getInstance().getResource("messages.yml");
                 OutputStream out = new FileOutputStream(usersFile)) {
                ByteStreams.copy(in, out);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(usersFile);
    }

}
