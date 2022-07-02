package me.nekocloud.vkbot.command.account;

import me.nekocloud.vkbot.api.objects.message.Message;
import me.nekocloud.vkbot.bot.VkBot;
import me.nekocloud.vkbot.command.VkCommand;
import me.nekocloud.vkbot.user.VkUser;
import org.jetbrains.annotations.NotNull;

public class AccountSettingsCommand extends VkCommand {

    public AccountSettingsCommand() {
        super("настройка", "настры", "настроить", "settings", "управление");
        setListenOnChats(false);
        setShouldLinkAccount(true);
        setOnlyPrivateMessages(true);
    }

    @Override
    protected void execute(VkUser vkUser, @NotNull Message message, @NotNull String[] args, @NotNull VkBot vkBot) {

    }
}
