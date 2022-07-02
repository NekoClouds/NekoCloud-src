package me.nekocloud.vkbot.command.account;

import lombok.val;
import me.nekocloud.core.common.auth.CoreAuth;
import me.nekocloud.vkbot.api.objects.message.Message;
import me.nekocloud.vkbot.bot.VkBot;
import me.nekocloud.vkbot.command.VkCommand;
import me.nekocloud.vkbot.user.VkUser;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;

public class AccountRecoveryCommand extends VkCommand {

    public AccountRecoveryCommand() {
        super("восстановить");
        
        setOnlyPrivateMessages(true);
    }

    @Override
    protected void execute(@NotNull VkUser vkUser, @NotNull Message message, @NotNull String[] args, @NotNull VkBot vkBot) {
        val authPlayer = CoreAuth.getAuthManager().loadOrCreate(vkUser.getPrimaryAccountName());
        val player = authPlayer.getHandle();

        if (player != null && player.isOnline()) {
            val lang = player.getLanguage();
            player.disconnectLocale("GAME_DATA_UPDATE_KICK");
        }

        String newPassword = RandomStringUtils.randomAlphanumeric(8);
        //authPlayer.setNewPassword(newPassword);

        vkBot.printAndDeleteMessage(message.getPeerId(), "❗ Восстановление прошло успешно. Ваш новый пароль: " + newPassword);
    }
}
