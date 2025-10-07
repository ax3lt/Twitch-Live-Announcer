package it.ax3lt.tla.placeholder;

import it.ax3lt.tla.TwitchLiveAnnouncerPlugin;
import it.ax3lt.tla.core.PluginContext;
import it.ax3lt.tla.placeholder.placeholders.LivePlaceholder;
import it.ax3lt.tla.placeholder.placeholders.StatusPlaceholder;
import it.ax3lt.tla.placeholder.placeholders.StatusStringPlaceholder;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public final class PlaceholderManager extends PlaceholderExpansion {

    private final TwitchLiveAnnouncerPlugin plugin;

    public PlaceholderManager(TwitchLiveAnnouncerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (!PluginContext.get().getConfiguration().getBoolean("placeholders.enabled")) {
            return null;
        }

        if (params.startsWith("status")) {
            return new StatusPlaceholder(getIdentifier(), getAuthor(), getVersion()).onRequest(player, params);
        }
        if (params.startsWith("live_")) {
            return new LivePlaceholder(getIdentifier(), getAuthor(), getVersion()).onRequest(player, params);
        }
        if (params.startsWith("status_string_")) {
            return new StatusStringPlaceholder(getIdentifier(), getAuthor(), getVersion()).onRequest(player, params);
        }
        return null;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "tla";
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().isEmpty() ? "ax3lt" : plugin.getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }
}
