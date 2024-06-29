package it.ax3lt.Utils;

import it.ax3lt.BungeeManager.MessageSender;
import it.ax3lt.Main.TLA;
import it.ax3lt.Utils.Configs.ConfigUtils;
import it.ax3lt.Utils.Configs.MessagesConfigUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.util.ArrayList;
import java.util.List;

public class MessageUtils {

    public static void broadcastMessage(String message, String channelName) {
        if (TLA.bungeeMode) {
            if (message.contains("%link%")) {
                TextComponent textComponent = Component.text(
                                message.replace("%link%", MessagesConfigUtils.getString("url-text")))
                        .clickEvent(ClickEvent.openUrl("https://twitch.tv/" + channelName));
                MessageSender.sendRawBungeeMessage(GsonComponentSerializer.gson().serialize(textComponent));
            } else if (message.contains("%multistream%")) {
                String multiUrl = generateMultiUrlStreamLink();
                if (multiUrl != null) {
                    TextComponent textComponent = Component.text(
                                    message.replace("%multistream%", MessagesConfigUtils.getString("multi-url-text")))
                            .clickEvent(ClickEvent.openUrl(multiUrl));
                    MessageSender.sendRawBungeeMessage(GsonComponentSerializer.gson().serialize(textComponent));
                } else {
                    TLA.getInstance().getLogger().warning(MessagesConfigUtils.getString("invalidMultiStreamService"));
                }
            } else {
                MessageSender.sendBungeeMessage(message);
            }
        } else {
            if (message.contains("%link%")) {
                TLA.getInstance().getServer().getOnlinePlayers().forEach(player -> {
                    net.md_5.bungee.api.chat.TextComponent textComponent = new net.md_5.bungee.api.chat.TextComponent(message.replace("%link%", MessagesConfigUtils.getString("url-text")));
                    textComponent.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.OPEN_URL, "https://twitch.tv/" + channelName));
                    player.spigot().sendMessage(textComponent);
                });
            } else if (message.contains("%multistream%")) {
                String multiUrl = generateMultiUrlStreamLink();
                if (multiUrl != null) {
                    TLA.getInstance().getServer().getOnlinePlayers().forEach(player -> {
                        net.md_5.bungee.api.chat.TextComponent textComponent = new net.md_5.bungee.api.chat.TextComponent(message.replace("%multistream%", MessagesConfigUtils.getString("multi-url-text")));
                        textComponent.setClickEvent(new net.md_5.bungee.api.chat.ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.OPEN_URL, multiUrl));
                        player.spigot().sendMessage(textComponent);
                    });
                } else {
                    TLA.getInstance().getLogger().warning(MessagesConfigUtils.getString("invalidMultiStreamService"));
                }
            } else {
                TLA.getInstance().getServer().broadcastMessage(message);
            }
        }
    }


    // multipleStreamService:
    //  enabled: false
    //  broadcastTime: 60
    //  # Available services: 'multistream', 'multitwitch'
    //  type: 'multitwitch'
    //  # The URL to the service
    //  configuration: '%separator%%channels%'
    //  # The separator for the channels, example: 'channel1/channel2/channel3'
    //  separator: '/'
    //  # If you want to use a custom URL, set this to true, type will be ignored
    //  useCustomUrl: false
    //  baseUrl: 'https://customservice.com/'

    private static String generateMultiUrlStreamLink() {
        String configuration = ConfigUtils.getConfigString("multipleStreamService.configuration");
        String baseUrl = "";

        if (TLA.getInstance().getConfig().getBoolean("multipleStreamService.useCustomUrl")) {
            baseUrl = ConfigUtils.getConfigString("multipleStreamService.baseUrl");
        } else {
            String type = ConfigUtils.getConfigString("multipleStreamService.type");
            if (type.equalsIgnoreCase("multistream")) {
                baseUrl = "https://multistre.am";
            } else if (type.equalsIgnoreCase("multitwitch")) {
                baseUrl = "https://multitwitch.tv";
            }
        }

        if (baseUrl.isEmpty()) {
            return null;
        }

        List<String> channels = new ArrayList<>(StreamUtils.streams.keySet());
        StringBuilder channelsList = new StringBuilder(); // channel1/channel2/channel3
        for (String channel : channels) {
            channelsList.append(channel).append(ConfigUtils.getConfigString("multipleStreamService.separator"));
        }
        if (channelsList.length() > 0) {
            channelsList.setLength(channelsList.length() - 1);
        }

        return (baseUrl + configuration.replace("%channels%", channelsList.toString())).replaceAll("%separator%", ConfigUtils.getConfigString("multipleStreamService.separator"));
    }
}
