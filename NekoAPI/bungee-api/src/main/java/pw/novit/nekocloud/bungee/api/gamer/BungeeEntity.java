package pw.novit.nekocloud.bungee.api.gamer;

import me.nekocloud.base.locale.Language;
import net.md_5.bungee.api.chat.BaseComponent;

public interface BungeeEntity {
    void sendMessage(String msg);
    void sendMessage(BaseComponent component);
    void sendMessage(BaseComponent[] components);

    Language getLanguage();

    String getName();

    String getChatName();

    boolean isHuman();

    void sendMessageLocale(String key, Object... objects);
    void sendMessagesLocale(String key, Object... objects);
}
