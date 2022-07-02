package me.nekocloud.vkbot.command.feature;

import lombok.val;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.vkbot.api.objects.message.Message;
import me.nekocloud.vkbot.bot.VkBot;
import me.nekocloud.vkbot.command.VkCommand;
import me.nekocloud.vkbot.user.VkUser;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class TabCompleteCommand extends VkCommand {

    public TabCompleteCommand() {
        super("tab", "таб");
    }

    @Override
    protected void execute(VkUser vkUser, @NotNull Message message, @NotNull String[] args, @NotNull VkBot vkBot) {
        if (args.length == 0) {
            vkBot.printMessage(message.getPeerId(), "Ошибка, ты не указал префикс поиска");
            return;
        }

        val corePlayerList = NekoCore.getInstance().getOnlinePlayers().stream()
                .filter(player -> player.getName().toLowerCase().startsWith(args[0].toLowerCase())).toList();

        if (corePlayerList.size() == 0) {
            vkBot.printMessage(message.getPeerId(), "Ошибка, игроки по префиксу " + args[0] + " не найдены");
            return;
        }

        val stringBuilder = new StringBuilder();
        for (val player : corePlayerList) {
            stringBuilder.append(player.getName())
                    .append(corePlayerList.get(corePlayerList.size() - 1).equals(player) ? " " : ", ");
        }

        vkBot.printMessage(message.getPeerId(), "По твоем запросу найдено " + corePlayerList.size() + " игроков:\n" + stringBuilder.toString());
    }
}
