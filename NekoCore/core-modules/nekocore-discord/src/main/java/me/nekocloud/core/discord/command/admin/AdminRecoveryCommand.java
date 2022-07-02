package me.nekocloud.core.discord.command.admin;

import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.core.common.auth.CoreAuth;
import me.nekocloud.core.common.auth.CoreAuthPlayer;
import me.nekocloud.core.discord.command.DiscordCommand;
import me.nekocloud.core.discord.user.DiscordUser;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;

public class AdminRecoveryCommand extends DiscordCommand {

    public AdminRecoveryCommand() {
        super("разрег", "unreg", "unregister");

        setGroup(Group.ADMIN);

        setShouldLinkAccount(true);
        setOnlyPrivateMessages(true);
    }

    @Override
    protected void execute(String[] args, @NotNull User author, DiscordUser user, Message inputMessage, MessageChannel channel) {
        if (args.length == 0) {
            inputMessage.reply( "❗ Ошибка, используй: !unreg <ник>").queue();
            return;
        }

        CoreAuthPlayer coreAuthPlayer = CoreAuth.getAuthManager().loadOrCreate(args[0]);

        if (coreAuthPlayer == null) {
            inputMessage.reply("❗ Ошибка, данный игрок не зарегестрирован").queue();
            return;
        }

        String newPassword = RandomStringUtils.randomAlphanumeric(8);
//        coreAuthPlayer.setNewPassword(newPassword);
//
//        CoreAuthManager.INSTANCE.removeSession(coreAuthPlayer.getOfflineHandle());

        if (coreAuthPlayer.hasDiscordUser()) {
            DiscordUser targetDiscordUser = DiscordUser.getDiscordUser(coreAuthPlayer.getDiscordId());

            targetDiscordUser.removeLinkedAccount();
        }

        inputMessage.reply("Новый пароль игрока " + args[0] + " - " + newPassword).queue();
        channel.deleteMessageById(channel.getLatestMessageIdLong()).queue();
    }
}
