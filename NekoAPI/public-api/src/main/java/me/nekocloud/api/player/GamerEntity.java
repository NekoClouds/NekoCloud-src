package me.nekocloud.api.player;

import me.nekocloud.base.locale.Language;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * для команд
 */
public interface GamerEntity {

    CommandSender getCommandSender();

    void sendMessage(String message);
    void sendMessages(List<String> messages);

    void sendMessageLocale(String key, Object... objects);
    void sendMessagesLocale(String key, Object... objects);

    Language getLanguage();

    String getName();

    String getChatName();

    boolean isHuman();
}
