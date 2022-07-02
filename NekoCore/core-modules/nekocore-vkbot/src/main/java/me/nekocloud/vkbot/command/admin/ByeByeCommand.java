package me.nekocloud.vkbot.command.admin;

import com.google.common.base.Joiner;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.chat.ChatColor;
import me.nekocloud.vkbot.api.objects.message.Message;
import me.nekocloud.vkbot.bot.VkBot;
import me.nekocloud.vkbot.command.VkCommand;
import me.nekocloud.vkbot.user.VkUser;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ByeByeCommand extends VkCommand {

    public ByeByeCommand() {
        super("бб", "пошёлнахуй");

        setGroup(Group.ADMIN);

        setOnlyChats(true);
        setShouldLinkAccount(true);
    }

    @Override
    protected void execute(VkUser vkUser, @NotNull Message message, @NotNull String[] args, @NotNull VkBot vkBot) {
        if (message.getForwardedMessages().isEmpty()) {
            vkBot.printMessage(message.getPeerId(), "Ошибка, ты не переслал сообщение необходимого пользователя!");
            return;
        }

        if (args.length == 0) {
            vkBot.printMessage(message.getPeerId(), "Ошибка, ты не указал причину кика");
            return;
        }

        Message forwardedMessage = message.getForwardedMessages().get(0);
        vkBot.kick(message.getChatId(), forwardedMessage.getUserId());

        String reason = Joiner.on(" ").join(Arrays.copyOfRange(args, 0, args.length));
        vkBot.printMessage(message.getPeerId(), "Пользователь [id" + forwardedMessage.getUserId() + "|id" + forwardedMessage.getUserId()
                + "] был кикнут из беседы \n\nПричина: " + reason + "\nКикнул: "
                + ChatColor.stripColor(NekoCore.getInstance().getOfflinePlayer(vkUser.getPrimaryAccountName()).getDisplayName()));
    }
}
