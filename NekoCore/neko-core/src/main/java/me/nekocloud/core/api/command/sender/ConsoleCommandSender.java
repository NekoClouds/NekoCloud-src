package me.nekocloud.core.api.command.sender;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.locale.Language;
import me.nekocloud.core.api.chat.ChatColor;
import me.nekocloud.core.api.chat.ChatMessageType;
import me.nekocloud.core.api.chat.component.BaseComponent;
import me.nekocloud.core.api.chat.component.TextComponent;
import me.nekocloud.core.api.command.CommandSender;
import me.nekocloud.core.api.command.CommandSendingType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Log4j2
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ConsoleCommandSender implements CommandSender {

    @Getter
    static ConsoleCommandSender instance      = new ConsoleCommandSender();

    String name                               = ("NekoCore");

    CommandSendingType sendingType            = CommandSendingType.CONSOLE;
    Language lang                             = Language.DEFAULT;
    Group group                               = Group.ADMIN;

    @Override
    public String getDisplayName() {
        return ChatColor.RED + name;
    }

    @Override
    public Language getLanguage() {
        return Language.DEFAULT;
    }

    @Override
    public boolean isHuman() {
        return false;
    }

    @Override
    public void sendMessage(final @NotNull ChatMessageType messageType, BaseComponent[] baseComponents) {
        log.info(TextComponent.toLegacyText(baseComponents));
    }

    @Override
    public void sendMessageLocale(String key, Object... replaced) {
        this.sendMessage(getLanguage().getMessage(key, replaced));
    }

    @Override
    public void sendMessagesLocale(String key, Object... replaced) {
        List<String> messages = getLanguage().getList(key, replaced);
        messages.forEach(this::sendMessage);
    }
}
