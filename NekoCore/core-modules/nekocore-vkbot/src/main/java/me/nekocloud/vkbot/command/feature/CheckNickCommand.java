package me.nekocloud.vkbot.command.feature;

import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.chat.ChatColor;
import me.nekocloud.vkbot.api.objects.message.Message;
import me.nekocloud.vkbot.bot.VkBot;
import me.nekocloud.vkbot.command.VkCommand;
import me.nekocloud.vkbot.user.VkUser;
import org.jetbrains.annotations.NotNull;

public class CheckNickCommand extends VkCommand {

    public CheckNickCommand() {
        super("ник", "никнейм");

        setOnlyChats(true);
    }

    @Override
    protected void execute(VkUser vkUser, @NotNull Message message, @NotNull String[] args, @NotNull VkBot vkBot) {
        if (message.getForwardedMessages().isEmpty()) {
            vkBot.printMessage(message.getPeerId(), "❗ Ошибка, Ты не переслал сообщение необходимого пользователя!");
            return;
        }

        Message forwardedMessage = message.getForwardedMessages().get(0);
        VkUser targetVkUser = VkUser.getVkUser(forwardedMessage.getUserId());

        if (!targetVkUser.hasPrimaryAccount()) {
            vkBot.printMessage(message.getPeerId(), "❗ Ошибка, пользователь [id" + targetVkUser.getVkId() + "|id" + targetVkUser.getVkId() + "] не привязал игровой аккаунт к своему VK!");
            return;
        }

        String playerName = targetVkUser.getPrimaryAccountName();

        vkBot.replyMessage(message.getPeerId(), "❗ Игровой ник пользователя [id" + targetVkUser.getVkId() + "|id" + targetVkUser.getVkId() + "] - " +
                ChatColor.stripColor(NekoCore.getInstance().getOfflinePlayer(playerName).getDisplayName()));
    }

}
