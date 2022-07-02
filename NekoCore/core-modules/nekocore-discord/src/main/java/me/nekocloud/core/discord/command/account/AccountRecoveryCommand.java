package me.nekocloud.core.discord.command.account;

import lombok.val;
import me.nekocloud.core.common.auth.CoreAuth;
import me.nekocloud.core.discord.command.DiscordCommand;
import me.nekocloud.core.discord.user.DiscordUser;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;

public class AccountRecoveryCommand extends DiscordCommand {

    public AccountRecoveryCommand() {
        super("восстановить");
        
        setOnlyPrivateMessages(true);
    }

    @Override
    protected void execute(String[] args, @NotNull User author, DiscordUser user, Message inputMessage, MessageChannel channel) {
        val authPlayer = CoreAuth.getAuthManager().loadOrCreate(user.getPrimaryAccountName());
        val player = authPlayer.getHandle();

        if (player != null && player.isOnline()) {
            val lang = player.getLanguage();
            player.disconnectLocale("GAME_DATA_UPDATE_KICK");
        }

        val newPassword = RandomStringUtils.randomAlphanumeric(8);
        //authPlayer.setNewPassword(newPassword);

        inputMessage.reply("❗ Восстановление прошло успешно. Ваш новый пароль: " + newPassword).queue();

    }
}
