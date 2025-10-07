package it.ax3lt.tla.message;

import it.ax3lt.tla.config.ConfigurationFormatter;
import it.ax3lt.tla.config.MessageConfiguration;
import it.ax3lt.tla.core.PluginContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class MessageService {

    private MessageService() {
    }

    public static void broadcastMessage(String message, String channelName) {
        if (PluginContext.get().isBungeeMode()) {
            sendBungeeMessage(message, channelName);
        } else {
            sendSpigotMessage(message, channelName);
        }
    }

    private static void sendBungeeMessage(String message, String channelName) {
        if (message.contains("%link%")) {
            Component component = Component.text(message.replace("%link%", MessageConfiguration.getMessage("url-text")))
                    .clickEvent(ClickEvent.openUrl("https://twitch.tv/" + channelName));
            PluginContext.get().getBungeeService().sendRawMessage(GsonComponentSerializer.gson().serialize(component));
        } else if (message.contains("%multistream%")) {
            String multiUrl = generateMultiStreamUrl();
            if (multiUrl != null) {
                Component component = Component.text(message.replace("%multistream%", MessageConfiguration.getMessage("multi-url-text")))
                        .clickEvent(ClickEvent.openUrl(multiUrl));
                PluginContext.get().getBungeeService().sendRawMessage(GsonComponentSerializer.gson().serialize(component));
            } else {
                PluginContext.get().getPlugin().getLogger().warning(MessageConfiguration.getMessage("invalidMultiStreamService"));
            }
        } else {
            PluginContext.get().getBungeeService().sendMessage(message);
        }
    }

    private static void sendSpigotMessage(String message, String channelName) {
        if (message.contains("%link%")) {
            for (Player player : PluginContext.get().getPlugin().getServer().getOnlinePlayers()) {
                TextComponent component = new TextComponent(message.replace("%link%", MessageConfiguration.getMessage("url-text")));
                component.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(Action.OPEN_URL, "https://twitch.tv/" + channelName));
                player.spigot().sendMessage(component);
            }
        } else if (message.contains("%multistream%")) {
            String multiUrl = generateMultiStreamUrl();
            if (multiUrl != null) {
                for (Player player : PluginContext.get().getPlugin().getServer().getOnlinePlayers()) {
                    TextComponent component = new TextComponent(message.replace("%multistream%", MessageConfiguration.getMessage("multi-url-text")));
                    component.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(Action.OPEN_URL, multiUrl));
                    player.spigot().sendMessage(component);
                }
            } else {
                PluginContext.get().getPlugin().getLogger().warning(MessageConfiguration.getMessage("invalidMultiStreamService"));
            }
        } else {
            PluginContext.get().getPlugin().getServer().broadcastMessage(message);
        }
    }

    private static String generateMultiStreamUrl() {
        String configuration = ConfigurationFormatter.getConfigString("multipleStreamService.configuration");
        String baseUrl;

        if (PluginContext.get().getConfiguration().getBoolean("multipleStreamService.useCustomUrl")) {
            baseUrl = ConfigurationFormatter.getConfigString("multipleStreamService.baseUrl");
        } else {
            String type = ConfigurationFormatter.getConfigString("multipleStreamService.type");
            if (type.equalsIgnoreCase("multistream")) {
                baseUrl = "https://multistre.am";
            } else if (type.equalsIgnoreCase("multitwitch")) {
                baseUrl = "https://multitwitch.tv";
            } else {
                baseUrl = "";
            }
        }

        if (baseUrl.isEmpty()) {
            return null;
        }

        List<String> channels = new ArrayList<>(PluginContext.get().getStreamService().getActiveStreams().keySet());
        String separator = ConfigurationFormatter.getConfigString("multipleStreamService.separator");
        StringBuilder builder = new StringBuilder();
        for (String channel : channels) {
            builder.append(channel).append(separator);
        }
        if (builder.length() > 0) {
            builder.setLength(builder.length() - separator.length());
        }

        return (baseUrl + configuration.replace("%channels%", builder.toString())).replace("%separator%", separator);
    }
}
