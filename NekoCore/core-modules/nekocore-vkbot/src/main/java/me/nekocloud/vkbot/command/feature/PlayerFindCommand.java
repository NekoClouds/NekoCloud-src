package me.nekocloud.vkbot.command.feature;

import lombok.val;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.TimeUtil;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.chat.ChatColor;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.vkbot.api.objects.keyboard.button.KeyboardButtonColor;
import me.nekocloud.vkbot.api.objects.keyboard.button.action.TextButtonAction;
import me.nekocloud.vkbot.api.objects.message.Message;
import me.nekocloud.vkbot.bot.VkBot;
import me.nekocloud.vkbot.command.VkCommand;
import me.nekocloud.vkbot.user.VkUser;
import org.jetbrains.annotations.NotNull;

public class PlayerFindCommand extends VkCommand {

    public PlayerFindCommand() {
        super("find", "search", "найти");
    }

    @Override
    protected void execute(VkUser vkUser, @NotNull Message message, @NotNull String[] args, @NotNull VkBot vkBot) {
        if (args.length == 0) {
            notEnoughArguments("/find <игрок>");
            return;
        }

        val name = args[0];
        if (!hasIdentifier(name)) return;
        CorePlayer targetPlayer = NekoCore.getInstance().getPlayer(name);
        if (targetPlayer == null) {
            targetPlayer = NekoCore.getInstance().getOfflinePlayer(name);

            val lastOnline = TimeUtil.leftTime(Language.DEFAULT,
                    System.currentTimeMillis() - targetPlayer.getLastOnline());
            vkBot.printMessage(message.getPeerId(), ChatColor.stripColor("❗ Игрок " + targetPlayer.getDisplayName() +
                    " оффлайн, последний раз он был на сервере " + lastOnline +" назад")); // мне лень
            return;
        }

        val senderPlayer = NekoCore.getInstance().getOfflinePlayer(vkUser.getPrimaryAccountName());
        if (senderPlayer.isStaff()) {
            new Message()
                    .peerId(message.getPeerId())
                    .body(ChatColor.stripColor(targetPlayer.getDisplayName()) + " находится на сервере " + targetPlayer.getBukkit().getName())
                    .keyboard(true, true)
                    .button(KeyboardButtonColor.PRIMARY, 0, new TextButtonAction("/ip " + targetPlayer.getName(), "Узнать IP игрока " + targetPlayer.getName()))
                    .message().send(vkBot);
        } else {
            new Message()
                    .peerId(message.getPeerId())
                    .body(ChatColor.stripColor(targetPlayer.getDisplayName()) + " находится на сервере " + targetPlayer.getBukkit().getName())
                    .send(vkBot);
        }

    }

}
