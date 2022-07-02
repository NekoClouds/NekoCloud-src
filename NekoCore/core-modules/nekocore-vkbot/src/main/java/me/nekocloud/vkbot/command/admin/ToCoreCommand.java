package me.nekocloud.vkbot.command.admin;

import com.google.common.base.Joiner;
import lombok.val;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.command.sender.VkbotCommandSender;
import me.nekocloud.vkbot.api.objects.message.Message;
import me.nekocloud.vkbot.bot.VkBot;
import me.nekocloud.vkbot.command.VkCommand;
import me.nekocloud.vkbot.user.VkUser;
import org.jetbrains.annotations.NotNull;

public class ToCoreCommand extends VkCommand {

    public ToCoreCommand() {
        super("nekocloud");

        setShouldLinkAccount(true);
        setGroup(Group.OWNER);
    }

    @Override
    protected void execute(VkUser vkUser, @NotNull Message message, @NotNull String[] args, @NotNull VkBot vkBot) {
        if (args.length == 0) {
            vkBot.printMessage(message.getPeerId(), "❗ Ошибка, используй: /nekocloud <команда кора>");
            return;
        }

        val vkbotCommandSender = VkbotCommandSender.getInstance();
        vkbotCommandSender.setLastUserId(message.getPeerId());

        val command = Joiner.on(" ").join(args);

        NekoCore.getInstance().getCommandManager().dispatchCommand(vkbotCommandSender, command);
    }

}
