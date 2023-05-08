package it.ax3lt.PlaceHolderApiExpansion;

import it.ax3lt.Main.TLA;
import it.ax3lt.PlaceHolderApiExpansion.PlaceHolders.LivePlaceholder;
import it.ax3lt.PlaceHolderApiExpansion.PlaceHolders.StatusPlaceholder;
import it.ax3lt.PlaceHolderApiExpansion.PlaceHolders.StatusStringPlaceholder;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PlaceHolderManager extends PlaceholderExpansion {

    @Override
    public String onRequest(OfflinePlayer player, String params) {

        if (!TLA.getInstance().getConfig().getBoolean("placeholders.enabled"))
            return null;

        if (params.startsWith("status")) {
            return new StatusPlaceholder(getIdentifier(), getAuthor(), getVersion()).onRequest(player, params);
        }
        if (params.startsWith("live_")) {
            return new LivePlaceholder(getIdentifier(), getAuthor(), getVersion()).onRequest(player, params);
        }
        if (params.startsWith("status_string_")) {
            return new StatusStringPlaceholder(getIdentifier(), getAuthor(), getVersion()).onRequest(player, params);
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
        return "1.4";
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }
}
