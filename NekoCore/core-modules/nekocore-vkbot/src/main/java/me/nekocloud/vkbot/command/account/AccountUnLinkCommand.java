package me.nekocloud.vkbot.command.account;

import me.nekocloud.vkbot.api.objects.message.Message;
import me.nekocloud.vkbot.bot.VkBot;
import me.nekocloud.vkbot.command.VkCommand;
import me.nekocloud.vkbot.user.VkUser;
import org.jetbrains.annotations.NotNull;

public class AccountUnLinkCommand extends VkCommand {

    public AccountUnLinkCommand() {
        super("отвязать", "отвязка", "отвезать", "отвяжи", "отвежи");

        setShouldLinkAccount(true);
        setOnlyPrivateMessages(true);
    }

    @Override
    protected void execute(VkUser vkUser, @NotNull Message message, @NotNull String[] args, @NotNull VkBot vkBot) {
        if (args.length == 0) {
            notEnoughArguments("!отвязать <ник>");
            return;
        }

        //для даунов убираем еще скобочки
        String playerName = trimForNoobs(args[0]);

        if (!vkUser.hasLinkedAccount(playerName)) {
            vkBot.printMessage(message.getPeerId(), "❗ К Вашему VK не привязан аккаунт с ником " + playerName);
            return;
        }

        vkUser.removeLinkedAccount();

        vkBot.printMessage(message.getPeerId(), "❗ Вы отвязали аккаунт " + playerName + " от своего VK." +
                "\nТеперь он в опасности, поскольку больше не защищен двухфакторной авторизацией." +
                "\n\n\uD83D\uDD25Напоминаем, что у нас запрещены передачи аккаунтов а также коммерческая деятельность, связанная с ними");
    }
}
