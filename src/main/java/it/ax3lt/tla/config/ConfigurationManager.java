package it.ax3lt.tla.config;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import it.ax3lt.tla.TwitchLiveAnnouncerPlugin;
import it.ax3lt.tla.core.PluginContext;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class ConfigurationManager {

    private static final DateTimeFormatter BACKUP_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");

    private final TwitchLiveAnnouncerPlugin plugin;

    public ConfigurationManager(TwitchLiveAnnouncerPlugin plugin) {
        this.plugin = plugin;
    }

    public YamlDocument loadConfig() throws IOException {
        File file = new File(plugin.getDataFolder(), "config.yml");
        return YamlDocument.create(
                file,
                Objects.requireNonNull(plugin.getResource("config.yml")),
                GeneralSettings.DEFAULT,
                LoaderSettings.builder().setAutoUpdate(true).build(),
                DumperSettings.DEFAULT,
                UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")).build()
        );
    }

    public YamlDocument loadMessages() throws IOException {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        return YamlDocument.create(
                file,
                Objects.requireNonNull(plugin.getResource("messages.yml")),
                GeneralSettings.DEFAULT,
                LoaderSettings.builder().setAutoUpdate(true).build(),
                DumperSettings.DEFAULT,
                UpdaterSettings.builder().setVersioning(new BasicVersioning("messages-version")).build()
        );
    }

    public void scheduleBackups() {
        if (!PluginContext.get().getConfiguration().getBoolean("backupConfig.enabled")) {
            return;
        }

        String mode = Objects.requireNonNull(PluginContext.get().getConfiguration().getString("backupConfig.when"));
        if ("daily".equalsIgnoreCase(mode)) {
            scheduleDailyBackups();
        } else if ("startup".equalsIgnoreCase(mode)) {
            performStartupBackups();
        }
    }

    private void scheduleDailyBackups() {
        new BukkitRunnable() {
            @Override
            public void run() {
                String configuredTime = PluginContext.get().getConfiguration().getString("backupConfig.time");
                if (configuredTime == null) {
                    cancel();
                    return;
                }

                String now = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
                if (!now.equalsIgnoreCase(configuredTime)) {
                    return;
                }

                backupWithFeedback("config.yml");
                backupWithFeedback("messages.yml");
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 1200L);
    }

    private void performStartupBackups() {
        backupWithFeedback("config.yml");
        backupWithFeedback("messages.yml");
    }

    private void backupWithFeedback(String fileName) {
        if (!backupFile(fileName)) {
            plugin.getServer().getConsoleSender().sendMessage(createBackupErrorMessage(fileName));
        }
    }

    private boolean backupFile(String fileName) {
        File backupFolder = new File(plugin.getDataFolder(), "backups");
        if (!backupFolder.exists() && !backupFolder.mkdirs()) {
            return false;
        }

        File sourceFile = new File(plugin.getDataFolder(), fileName);
        if (!sourceFile.exists()) {
            return false;
        }

        String timestamp = LocalDateTime.now().format(BACKUP_FORMATTER);
        Path destination = new File(backupFolder, fileName + "." + timestamp).toPath();
        try {
            Files.copy(sourceFile.toPath(), destination);
            return true;
        } catch (IOException exception) {
            plugin.getLogger().severe("Failed to backup file " + fileName + ": " + exception.getMessage());
            return false;
        }
    }

    public String createParametersErrorMessage(String errorMessage) {
        return MessageConfiguration.getMessage("parameters_error").replace("%message%", errorMessage);
    }

    private String createBackupErrorMessage(String fileName) {
        return MessageConfiguration.getMessage("backup_error").replace("%file%", fileName);
    }
}
