package it.ax3lt.Utils;

import it.ax3lt.BungeeManager.MessageSender;
import it.ax3lt.Main.TLA;
import it.ax3lt.Utils.Configs.MessagesConfigUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public class MessageUtils {

    public static void broadcastMessage(String message, String channelName) {
        if (TLA.bungeeMode) {
            if (message.contains("%link%")) {
                TextComponent textComponent = Component.text(message.replace("%link%", MessagesConfigUtils.getString("url-text")))
                        .clickEvent(ClickEvent.openUrl("https://twitch.tv/" + channelName));
                MessageSender.sendRawBungeeMessage(GsonComponentSerializer.gson().serialize(textComponent));
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
            } else {
                TLA.getInstance().getServer().broadcastMessage(message);
            }
        }
    }
}
