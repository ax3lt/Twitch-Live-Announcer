package it.ax3lt.PlaceHolderApiExpansion.PlaceHolders;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import it.ax3lt.Main.TLA;
import it.ax3lt.Utils.Configs.ConfigUtils;
import it.ax3lt.Utils.Configs.MessagesConfigUtils;
import it.ax3lt.Utils.StreamUtils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class StatusPlaceholder extends PlaceholderExpansion {
    String identifier, author, version;

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

        UUID playerUUID = player.getUniqueId();

        // Check in config file if the user is linked
        Section linked_users = TLA.config.getSection("linked_users");
        if (linked_users != null && linked_users.contains(playerUUID.toString())) {
            List<String> lowerCaseStreams = linked_users.getStringList(playerUUID.toString()).stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());

            if (!lowerCaseStreams.isEmpty()) {
                List<String> streamers = StreamUtils.streams.keySet().stream()
                        .map(String::toLowerCase)
                        .collect(Collectors.toList());
                for (String s : lowerCaseStreams) {
                    if(streamers.contains(s))
                        return ConfigUtils.getConfigString("placeholders.live");
                }
                return ConfigUtils.getConfigString("placeholders.offline");
            }
        }
        return MessagesConfigUtils.getString("link_inexistent");
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
