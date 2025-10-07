package it.ax3lt.tla.update;

import it.ax3lt.tla.TwitchLiveAnnouncerPlugin;
import it.ax3lt.tla.config.MessageConfiguration;
import it.ax3lt.tla.placeholder.PlaceholderManager;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public final class PluginUpdateChecker {

    private static final int RESOURCE_ID = 107784;

    private final TwitchLiveAnnouncerPlugin plugin;

    public PluginUpdateChecker(TwitchLiveAnnouncerPlugin plugin) {
        this.plugin = plugin;
    }

    public void checkForUpdates() {
        if (!plugin.getConfig().getBoolean("check_updates")) {
            return;
        }

        fetchVersion(version -> {
            if (!plugin.getDescription().getVersion().equalsIgnoreCase(version)) {
                plugin.getServer().getConsoleSender().sendMessage(
                        MessageConfiguration.getMessage("update_available").replace("%link%", "https://www.spigotmc.org/resources/107784")
                );
            }
        });
    }

    private void fetchVersion(Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + RESOURCE_ID).openStream();
                 Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
                plugin.getLogger().info(MessageConfiguration.getMessage("update_check_error"));
            }
        });
    }

    public void registerPlaceholderExpansion() {
        new PlaceholderManager(plugin).register();
    }
}
