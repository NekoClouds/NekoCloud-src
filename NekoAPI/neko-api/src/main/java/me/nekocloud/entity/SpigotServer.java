package me.nekocloud.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import me.nekocloud.api.player.Spigot;
import me.nekocloud.nekoapi.loader.NekoAPI;
import me.nekocloud.base.gamer.GamerAPI;
import me.nekocloud.base.locale.Language;
import org.bukkit.command.CommandSender;

import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public final class SpigotServer implements Spigot {

    NekoAPI mainPlugin;
    String name;

    @Override
    public CommandSender getCommandSender() {
        return mainPlugin.getServer().getConsoleSender();
    }

    @Override
    public void sendMessage(String message) {
        getCommandSender().sendMessage(message);
    }

    @Override
    public void sendMessages(List<String> messages) {
        messages.forEach(this::sendMessage);
    }

    @Override
    public void sendMessageLocale(String key, Object... objects) {
        sendMessage(getLanguage().getMessage(key, objects));
    }

    @Override
    public void sendMessagesLocale(String key, Object... objects) {
        sendMessages(getLanguage().getList(key, objects));
    }

    @Override
    public Language getLanguage() {
        return Language.DEFAULT;
    }

    @Override
    public String getChatName() {
        return "console-" + name;
    }

    @Override
    public boolean isHuman() {
        return false;
    }

    @Override
    public void broadcast(String message) {
        sendMessage(message);
        GamerAPI.getGamers().values().forEach(gamer -> gamer.sendMessage(message));
    }

    @Override
    public void broadcastLocale(String key, Object... objects) {
        sendMessageLocale(key, objects);
        GamerAPI.getGamers().values().forEach(gamer -> gamer.sendMessageLocale(key, objects));
    }
}
