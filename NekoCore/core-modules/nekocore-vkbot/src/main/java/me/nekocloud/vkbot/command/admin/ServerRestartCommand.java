package me.nekocloud.vkbot.command.admin;

import lombok.val;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.core.NekoCore;
import me.nekocloud.vkbot.api.objects.message.Message;
import me.nekocloud.vkbot.bot.VkBot;
import me.nekocloud.vkbot.command.VkCommand;
import me.nekocloud.vkbot.user.VkUser;
import org.jetbrains.annotations.NotNull;

public class ServerRestartCommand extends VkCommand {

    public ServerRestartCommand() {
        super("serverrestart", "reloadserver", "рестарт", "serverstop", "stopserver", "stop");

        setGroup(Group.OWNER);
        setShouldLinkAccount(true);
        setOnlyPrivateMessages(true);
    }

    @Override
    protected void execute(VkUser vkUser, @NotNull Message message, @NotNull String[] args, @NotNull VkBot vkBot) {
        if (args.length == 0) {
            vkBot.printMessage(message.getPeerId(), "❗ Ошибка, используй: !рестарт <имя сервера>");
            return;
        }

        val abstractServer = NekoCore.getInstance().getBukkit(args[0]);

        if (abstractServer == null) {
            vkBot.printMessage(message.getPeerId(), "❗ Ошибка, данный сервер не существует или не подключен к Core!");
            return;
        }
//        abstractServer.restart("Сервер был перезагружен через VK бота");
        vkBot.printMessage(message.getPeerId(), "Сервер " + abstractServer.getName() + " был отправлен нахуй!");
    }

}
