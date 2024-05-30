package it.ax3lt.Utils;

import it.ax3lt.Main.TLA;
import it.ax3lt.Utils.Configs.MessagesConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

// From: https://www.spigotmc.org/wiki/creating-an-update-checker-that-checks-for-updates
public class UpdateChecker {

    private final int resourceId = 107784;

    public void checkUpdate()
    {
        getVersion(version -> {
            if (!TLA.getInstance().getDescription().getVersion().equalsIgnoreCase(version)) {
                TLA.getInstance().getServer().getConsoleSender().sendMessage(MessagesConfigUtils.getString("update_available")
                        .replace("%link%", "https://www.spigotmc.org/resources/107784"));
            }
        });
    }

    public void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(TLA.getInstance(), () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
                TLA.getInstance().getLogger().info(MessagesConfigUtils.getString("update_check_error"));
            }
        });
    }
}
 