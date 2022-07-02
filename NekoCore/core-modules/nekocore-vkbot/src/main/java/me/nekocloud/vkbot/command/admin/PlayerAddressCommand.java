package me.nekocloud.vkbot.command.admin;

import lombok.val;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.chat.ChatColor;
import me.nekocloud.vkbot.api.objects.message.Message;
import me.nekocloud.vkbot.bot.VkBot;
import me.nekocloud.vkbot.command.VkCommand;
import me.nekocloud.vkbot.user.VkUser;
import org.jetbrains.annotations.NotNull;

public class PlayerAddressCommand extends VkCommand {

    public PlayerAddressCommand() {
        super("ip", "ип", "адрес", "address");

        setGroup(Group.ADMIN);

        setShouldLinkAccount(true);
        setOnlyPrivateMessages(true);

        setListenOnChats(false);
    }

    @Override
    protected void execute(VkUser vkUser, @NotNull Message message, @NotNull String[] args, @NotNull VkBot vkBot) {
        if (args.length == 0) {
            notEnoughArguments("!адрес <ник>");
            return;
        }

        if (!hasIdentifier(args[0])) return;
        val player = NekoCore.getInstance().getPlayer(args[0]);
        if (player == null) {
            playerOffline(args[0]);
            return;
        }

        vkBot.printAndDeleteMessage(message.getPeerId(),
                "❗ Информация об аккаунте: " + ChatColor.stripColor(player.getDisplayName()) +
                "\n IP: " + player.getIp().getHostAddress());
    }
}