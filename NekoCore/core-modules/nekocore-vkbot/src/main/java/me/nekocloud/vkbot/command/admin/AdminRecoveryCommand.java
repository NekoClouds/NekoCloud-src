package me.nekocloud.vkbot.command.admin;

import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.core.common.auth.CoreAuth;
import me.nekocloud.core.common.auth.CoreAuthPlayer;
import me.nekocloud.vkbot.api.objects.message.Message;
import me.nekocloud.vkbot.bot.VkBot;
import me.nekocloud.vkbot.command.VkCommand;
import me.nekocloud.vkbot.user.VkUser;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;

public class AdminRecoveryCommand extends VkCommand {

    public AdminRecoveryCommand() {
        super("разрег", "unreg", "unregister");

        setGroup(Group.ADMIN);

        setShouldLinkAccount(true);
        setOnlyPrivateMessages(true);
    }

    @Override
    protected void execute(VkUser vkUser, @NotNull Message message, @NotNull String[] args, @NotNull VkBot vkBot) {
        if (args.length == 0) {
            notEnoughArguments("!unreg <ник>");
            return;
        }

        CoreAuthPlayer coreAuthPlayer = CoreAuth.getAuthManager().loadOrCreate(args[0]);

        if (coreAuthPlayer == null) {
            vkBot.printMessage(message.getPeerId(), "❗ Ошибка, данный игрок не зарегестрирован");
            return;
        }

        if (coreAuthPlayer.getOfflineHandle().getGroup().getLevel() >= Group.ADMIN.getLevel()) {
            vkBot.printMessage(message.getPeerId(), "❗ Вы не можете взаимодействовать с данным игроком");
            return;
        }

        String newPassword = RandomStringUtils.randomAlphanumeric(8);
        //coreAuthPlayer.setNewPassword(newPassword);

        CoreAuth.getAuthManager().removeSession(coreAuthPlayer.getOfflineHandle());

        if (coreAuthPlayer.hasVKUser()) {
            VkUser targetVkUser = VkUser.getVkUser(coreAuthPlayer.getVkId());

            targetVkUser.removeLinkedAccount();
        }

        vkBot.printAndDeleteMessage(message.getPeerId(), "Новый пароль игрока " + args[0] + " - " + newPassword);
    }
}
