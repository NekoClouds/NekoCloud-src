package me.nekocloud.core.discord.command.account;

import lombok.val;
import me.nekocloud.core.common.auth.CoreAuth;
import me.nekocloud.core.discord.command.DiscordCommand;
import me.nekocloud.core.discord.user.DiscordUser;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

public class AccountUnLinkCommand extends DiscordCommand {

    public AccountUnLinkCommand() {
        super("отвязать", "отвязка", "отвезать", "отвяжи", "отвежи");

        setShouldLinkAccount(true);
        setOnlyPrivateMessages(true);
    }

    @Override
    protected void execute(String[] args, @NotNull User author, DiscordUser user, Message inputMessage, MessageChannel channel) {
        if (args.length == 0) {
            inputMessage.reply("❗ Ошибка в синтаксисе, используйте - !отвязать <ник>").queue();
            return;
        }

        //для даунов убираем еще скобочки
        val playerName = trimForNoobs(args[0]);

        if (!user.hasLinkedAccount(playerName)) {
            inputMessage.reply("❗ К Вашему Discord не привязан аккаунт с ником " + playerName).queue();
            return;
        }

        user.removeLinkedAccount();

        val authPlayer = CoreAuth.getAuthManager().loadOrCreate(playerName);
        //authPlayer.setNewDiscord(null);

        inputMessage.reply("❗ Вы отвязали аккаунт " + playerName + " от своего Discord." +
                "\nТеперь он в опасности, поскольку больше не защищен двухфакторной авторизацией." +
                "\n\n\uD83D\uDD25Напоминаем, что у нас запрещены передачи аккаунтов а также коммерческая деятельность, связанная с ними").queue();
    }
}
