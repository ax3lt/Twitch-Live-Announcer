package it.ax3lt.tla.placeholder.placeholders;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import it.ax3lt.tla.config.ConfigurationFormatter;
import it.ax3lt.tla.config.MessageConfiguration;
import it.ax3lt.tla.core.PluginContext;
import it.ax3lt.tla.stream.StreamService;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class StatusPlaceholder extends PlaceholderExpansion {

    private final String identifier;
    private final String author;
    private final String version;

    public StatusPlaceholder(String identifier, String author, String version) {
        this.identifier = identifier;
        this.author = author;
        this.version = version;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (player == null) {
            return "";
        }

        UUID playerUuid = player.getUniqueId();
        Section linkedUsers = PluginContext.get().getConfiguration().getSection("linked_users");
        if (linkedUsers != null && linkedUsers.contains(playerUuid.toString())) {
            List<String> linkedStreams = linkedUsers.getStringList(playerUuid.toString()).stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
            if (!linkedStreams.isEmpty()) {
                StreamService streamService = PluginContext.get().getStreamService();
                List<String> activeStreams = streamService.getActiveStreams().keySet().stream()
                        .map(String::toLowerCase)
                        .collect(Collectors.toList());
                for (String stream : linkedStreams) {
                    if (activeStreams.contains(stream)) {
                        return ConfigurationFormatter.getConfigString("placeholders.live");
                    }
                }
                return ConfigurationFormatter.getConfigString("placeholders.offline");
            }
        }
        return MessageConfiguration.getMessage("link_inexistent");
    }

    @Override
    public @NotNull String getIdentifier() {
        return identifier;
    }

    @Override
    public @NotNull String getAuthor() {
        return author;
    }

    @Override
    public @NotNull String getVersion() {
        return version;
    }
}
