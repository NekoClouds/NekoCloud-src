package me.nekocloud.vkbot.command.feature;

import lombok.extern.log4j.Log4j2;
import lombok.val;
import me.nekocloud.base.util.NumberUtils;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.connection.server.Bukkit;
import me.nekocloud.core.api.connection.server.IServerManager;
import me.nekocloud.vkbot.api.objects.message.Message;
import me.nekocloud.vkbot.bot.VkBot;
import me.nekocloud.vkbot.command.VkCommand;
import me.nekocloud.vkbot.user.VkUser;
import org.jetbrains.annotations.NotNull;

@Log4j2
public class OnlineCommand extends VkCommand {

    public OnlineCommand() {
        super("онлайн", "online");
    }

    @Override
    protected void execute(@NotNull VkUser vkUser, @NotNull Message message, @NotNull String[] args, @NotNull VkBot vkBot) {
        if (args.length == 0) {
            vkBot.printMessage(message.getPeerId(), "Общий онлайн сервера - " + NumberUtils.spaced(NekoCore.getInstance().getOnline()));

            return;
        }

        val serverManager = NekoCore.getInstance().getServerManager();
        if (args[0].startsWith("@") && args[0].length() > 1) {
            String serverPrefix = args[0].substring(1);

            vkBot.printMessage(message.getPeerId(), "Онлайн серверов по префиксу "
                    + serverPrefix.toUpperCase() + ": "
                    + NumberUtils.spaced(NekoCore.getInstance().getOnlineByServerPrefix(serverPrefix)));
            return;
        }

        val bukkit = serverManager.getBukkit(args[0]);
        if (bukkit == null) {
            vkBot.printMessage(message.getPeerId(), "❗ Ошибка, данный сервер не существует или не подключен к Core!");
            return;
        }

        vkBot.printMessage(message.getPeerId(), "Онлайн сервера " + bukkit.getName() + " - " + NumberUtils.spaced(bukkit.getOnline()));
    }
}
