package me.nekocloud.vkbot.command.admin;

import com.google.common.base.Joiner;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.chat.ChatColor;
import me.nekocloud.core.api.command.sender.VkbotCommandSender;
import me.nekocloud.vkbot.api.objects.message.Message;
import me.nekocloud.vkbot.bot.VkBot;
import me.nekocloud.vkbot.command.VkCommand;
import me.nekocloud.vkbot.user.VkUser;
import org.jetbrains.annotations.NotNull;

public class AlertCommand extends VkCommand {

    public AlertCommand() {
        super("alert", "объявить", "объявление");

        setGroup(Group.ADMIN);

        setShouldLinkAccount(true);
        setOnlyPrivateMessages(true);
    }

    @Override
    protected void execute(VkUser vkUser, @NotNull Message message, @NotNull String[] args, @NotNull VkBot vkBot) {
        if (args.length == 0) {
            notEnoughArguments("❗ Ошибка, используй: !alert <сообщение>");
            return;
        }

        String alertMessage = ChatColor.translateAlternateColorCodes('&', Joiner.on(" ").join(args));
        NekoCore.getInstance().getCommandManager().dispatchCommand(VkbotCommandSender.getInstance(), "alert " + alertMessage);

        vkBot.printMessage(message.getPeerId(), "✅ На все сервера отправлено сообщение:\n" +
                " ОБЪЯВЛЕНИЕ | " + ChatColor.stripColor(alertMessage));
    }

}
