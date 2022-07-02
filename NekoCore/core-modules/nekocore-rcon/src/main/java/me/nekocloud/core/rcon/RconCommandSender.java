package me.nekocloud.core.rcon;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.locale.Language;
import me.nekocloud.core.api.chat.ChatMessageType;
import me.nekocloud.core.api.chat.component.BaseComponent;
import me.nekocloud.core.api.chat.component.TextComponent;
import me.nekocloud.core.api.command.CommandSender;
import me.nekocloud.core.api.command.CommandSendingType;
import org.jetbrains.annotations.NotNull;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class RconCommandSender implements CommandSender {

    StringBuffer buffer = new StringBuffer();

    CommandSendingType commandSendingType = CommandSendingType.RCON;
    Language lang                         = Language.DEFAULT;
    Group group                           = Group.ADMIN;

    public String flush() {
        val result = buffer.toString();
        buffer.setLength(0);

        return result;
    }

    @Override
    public String getName() {
        return "CoreRcon";
    }

    @Override
    public String getDisplayName() {
        return getName();
    }

    @Override
    public CommandSendingType getSendingType() {
        return commandSendingType;
    }

    @Override
    public Language getLanguage() {
        return lang;
    }

    @Override
    public void sendMessage(@NotNull final String message) {
        buffer.append(message).append("\n");
    }

    @Override
    public void sendMessage(@NotNull final ChatMessageType messageType, final BaseComponent[] baseComponents) {
        sendMessage(TextComponent.toLegacyText(baseComponents));
    }

    @Override
    public boolean isHuman() {
        return false;
    }

    @Override
    public void sendMessageLocale(final String key, Object... replaced) {
        this.sendMessage(getLanguage().getMessage(key, replaced));
    }

    @Override
    public void sendMessagesLocale(final String key, Object... replaced) {
        val messages = getLanguage().getList(key, replaced);
        messages.forEach(this::sendMessage);
    }
}