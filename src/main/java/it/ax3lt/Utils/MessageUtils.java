package it.ax3lt.Utils;

import it.ax3lt.Main.StreamAnnouncer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class MessageUtils {

    public static void broadcastMessage(String message, String channelName) {
        if (message.contains("%link%")) {
            StreamAnnouncer.getInstance().getServer().getOnlinePlayers().forEach(player -> {
                TextComponent textComponent = new TextComponent(message.replace("%link%", ConfigUtils.getConfigString("url-text")));
                textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://twitch.tv/" + channelName));
                player.spigot().sendMessage(textComponent);
            });
        }
        else {
            StreamAnnouncer.getInstance().getServer().broadcastMessage(message);
        }
    }
}
