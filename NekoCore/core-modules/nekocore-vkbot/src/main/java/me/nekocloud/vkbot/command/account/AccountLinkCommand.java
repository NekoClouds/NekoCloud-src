package me.nekocloud.vkbot.command.account;

import me.nekocloud.core.common.NetworkManager;
import me.nekocloud.core.common.auth.CoreAuth;
import me.nekocloud.core.common.auth.CoreAuthPlayer;
import me.nekocloud.vkbot.api.objects.message.Message;
import me.nekocloud.vkbot.bot.VkBot;
import me.nekocloud.vkbot.command.VkCommand;
import me.nekocloud.vkbot.user.VkUser;
import org.jetbrains.annotations.NotNull;

public class AccountLinkCommand extends VkCommand {

    public AccountLinkCommand() {
        super("привязать", "привязка", "привезать", "аккаунт");

        setOnlyPrivateMessages(true);
    }

    @Override
    protected void execute(@NotNull VkUser vkUser, @NotNull Message message, @NotNull String[] args, @NotNull VkBot vkBot) {
        if (args.length < 2) {
            notEnoughArguments("!привязать <ник> <пароль>");
            vkBot.deleteMessages(message.getMessageId());
            return;
        }
        if (args.length >= 0)
            return; // TODO THIS

        //для даунов убираем еще скобочки
        String playerName = trimForNoobs(args[0]);
        String password = trimForNoobs(args[1]);

        if (vkUser.hasPrimaryAccount()) {
            vkBot.printMessage(message.getPeerId(), "❗ К Вашему VK уже привязан аккаунт");
            return;
        }

        if (vkUser.hasLinkedAccount(playerName)) {
            vkBot.printMessage(message.getPeerId(), "❗ К данному VK уже привязан аккаунт с ником " + playerName);
            return;
        }

        //ищем другого владельца данного аккаунта
        CoreAuthPlayer coreAuthPlayer = CoreAuth.getAuthManager().loadOrCreate(playerName);

        if (!CoreAuth.getAuthManager().hasPlayerAccount(NetworkManager.INSTANCE.getPlayerID(playerName))) {
            vkBot.printMessage(message.getPeerId(), "❗ Данный игрок не зарегистрирован на сервере! \n \nВНИМАНИЕ! Для привязки аккаунта Вы должны зайти на сервер как минимум 1 раз после выхода обновления");
            vkBot.deleteMessages(message.getMessageId());
            return;
        }

        //надо проверить что нам вернулся не пустой аккаунт, а реальный игрок
        if (coreAuthPlayer.hasVKUser()) {
            vkBot.printMessage(message.getPeerId(), "❗ Аккаунт с ником " + playerName + " уже привязан к другому VK");
            vkBot.deleteMessages(message.getMessageId());
            return;
        }

        //если хеши паролей не совпадают - отдельное
//        if (coreAuthPlayer.equalsPassword(password)) {
//
//            vkBot.printMessage(message.getPeerId(), "❗ Вы ошиблись при вводе пароля от аккаунта " + playerName);
//            vkBot.deleteMessages(message.getMessageId());
//            return;
//        }

        coreAuthPlayer.setVkId(vkUser.getVkId());

        vkUser.addLinkedAccount(playerName);
        vkBot.deleteMessages(message.getMessageId());

        vkBot.printMessage(message.getPeerId(), "✒ Вы успешно привязали аккаунт " + playerName + " к вашему профилю VKонтакте!");
    }
}
