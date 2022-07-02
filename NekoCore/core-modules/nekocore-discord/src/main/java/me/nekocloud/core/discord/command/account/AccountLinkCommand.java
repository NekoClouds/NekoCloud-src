package me.nekocloud.core.discord.command.account;

import lombok.val;
import me.nekocloud.core.common.NetworkManager;
import me.nekocloud.core.common.auth.CoreAuth;
import me.nekocloud.core.discord.command.DiscordCommand;
import me.nekocloud.core.discord.user.DiscordUser;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

public class AccountLinkCommand extends DiscordCommand {

    public AccountLinkCommand() {
        super("привязать", "привязка", "привезать", "аккаунт");

        setOnlyPrivateMessages(true);
    }

    @Override
    protected void execute(String[] args, @NotNull User author, DiscordUser user, Message inputMessage, MessageChannel channel) {
        if (args.length < 2) {
            inputMessage.reply("❗ Ошибка в синтаксисе, используй - !привязать <ник> <пароль>").queue();
            return;
        }

        //для даунов убираем еще скобочки
        val playerName = trimForNoobs(args[0]);
        val password = trimForNoobs(args[1]);

        if (user.hasPrimaryAccount()) {
            inputMessage.reply("❗ К Вашему Discord уже привязан аккаунт").queue();
            return;
        }

        if (user.hasLinkedAccount(playerName)) {
            inputMessage.reply("❗ К данному Discord уже привязан аккаунт с ником " + playerName).queue();
            return;
        }

        //ищем другого владельца данного аккаунта
        val authPlayer = CoreAuth.getAuthManager().loadOrCreate(playerName);

        if (!CoreAuth.getAuthManager().hasPlayerAccount(NetworkManager.INSTANCE.getPlayerID(playerName))) {
            inputMessage.reply("❗ Данный игрок не зарегистрирован на сервере! \n \nВНИМАНИЕ! Для привязки аккаунта Вы должны зайти на сервер как минимум 1 раз после выхода обновления").queue();
            return;
        }

        //надо проверить что нам вернулся не пустой аккаунт, а реальный игрок
        if (authPlayer.hasDiscordUser()) {
            inputMessage.reply("❗ Аккаунт с ником " + playerName + " уже привязан к другому Discord").queue();
            return;
        }

        //если хеши паролей не совпадают - отдельное
//        if (authPlayer.equalsPassword(password)) {
//
//            inputMessage.reply("❗ Вы ошиблись при вводе пароля от аккаунта " + playerName).queue();
//            return;
//        }

        authPlayer.setNewDiscord(user.getDiscordID());

        user.addLinkedAccount(playerName);

        inputMessage.reply("✒ Ты успешно привязал аккаунт " + playerName + " к Discord " + author.getAsTag()).queue();
    }
}