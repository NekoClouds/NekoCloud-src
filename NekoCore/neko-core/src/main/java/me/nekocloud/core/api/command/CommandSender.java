package me.nekocloud.core.api.command;

import lombok.val;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.locale.Language;
import me.nekocloud.core.api.chat.ChatMessageType;
import me.nekocloud.core.api.chat.component.BaseComponent;
import me.nekocloud.core.api.chat.component.TextComponent;
import org.jetbrains.annotations.NotNull;

public interface CommandSender {

    String getName();
    String getDisplayName();

    CommandSendingType getSendingType();

    Group getGroup();

    Language getLanguage();

    boolean isHuman();

    void sendMessage(final @NotNull ChatMessageType messageType, final BaseComponent[] baseComponents);

    default void sendMessage(final @NotNull String message) {
        sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacyText(message));
    }

    default void sendMessage(final @NotNull String... messages) {
        for (val message : messages) {
            sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacyText(message));
        }
    }

    default void sendMessageLocale(final String key, final Object... objects) {
        sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacyText(
                getLanguage().getMessage(key, objects)
        ));
    }

    default void sendMessagesLocale(final String key, final Object... objects){
        val messages = getLanguage().getList(key, objects);
        messages.forEach(this::sendMessage);
    }
}
