package it.ax3lt.tla.placeholder.placeholders;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import it.ax3lt.tla.config.MessageConfiguration;
import it.ax3lt.tla.core.PluginContext;
import it.ax3lt.tla.stream.StreamService;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public final class LivePlaceholder extends PlaceholderExpansion {

    private final String identifier;
    private final String author;
    private final String version;

    public LivePlaceholder(String identifier, String author, String version) {
        this.identifier = identifier;
        this.author = author;
        this.version = version;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        String[] split = params.split("_");
        if (split.length == 2) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(split[1]);
            UUID uuid = target.getUniqueId();
            Section linkedUsers = PluginContext.get().getConfiguration().getSection("linked_users");
            if (linkedUsers != null && linkedUsers.contains(uuid.toString())) {
                List<String> streams = linkedUsers.getStringList(uuid.toString());
                if (streams != null && !streams.isEmpty()) {
                    StreamService streamService = PluginContext.get().getStreamService();
                    for (String stream : streams) {
                        if (streamService.getActiveStreams().containsKey(stream)) {
                            return "true";
                        }
                    }
                    return "false";
                }
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
