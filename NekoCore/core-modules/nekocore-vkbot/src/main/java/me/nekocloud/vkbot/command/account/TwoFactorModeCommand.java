package me.nekocloud.vkbot.command.account;

import lombok.val;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.common.auth.CoreAuth;
import me.nekocloud.vkbot.api.objects.message.Message;
import me.nekocloud.vkbot.bot.VkBot;
import me.nekocloud.vkbot.command.VkCommand;
import me.nekocloud.vkbot.user.VkUser;
import org.jetbrains.annotations.NotNull;

// Её отключение делать не будем ибо нехуй.
// Мы заботимся о безопастности игроков
public class TwoFactorModeCommand extends VkCommand {

    public TwoFactorModeCommand() {
        super("2fa", "двух-этапка");

        setShouldLinkAccount(true);
        setOnlyPrivateMessages(true);
    }

    @Override
    protected void execute(VkUser vkUser, @NotNull Message message, @NotNull String[] args, @NotNull VkBot vkBot) {
        if (args.length < 1) {
            notEnoughArguments("/2fa <vk|discord> \n - Переключает режим двух-факторной авторизации.");
            return;
        }

        val corePlayer = NekoCore.getInstance().getOfflinePlayer(vkUser.getPrimaryAccountName());
        val authPlayer = CoreAuth.getAuthManager().loadOrCreate(corePlayer.getPlayerID());

        switch (args[0].toLowerCase()) {
            case "vkontakte", "vk", "вк", "вконтакте" -> {
                if (authPlayer.getAuthType().equals(0)) {
                    vkBot.printMessage(message.getPeerId(), "❗ Ошибка, у тебя уже включена двух-этапная авторизация по VK");
                    return;
                }
                authPlayer.setAuthType(0);
                vkBot.printMessage(message.getPeerId(), "❗ Ты успешно включил двух-этапную авторизацию по VK");
            }
            case "discord", "ds", "дс", "дискорд" -> {
                if (authPlayer.getAuthType().equals(1)) {
                    vkBot.printMessage(message.getPeerId(), "❗ Ошибка, у тебя уже включена двух-этапная авторизация по Discord");
                    return;
                }
                authPlayer.setAuthType(1);
                vkBot.printMessage(message.getPeerId(), "❗ Ты успешно включил двух-этапную авторизацию по Discord");
            }
            case "telegram", "tg", "тг", "телеграм" -> vkBot.printMessage(message.getPeerId(), "❗ Ошибка, данная функция еще в разработке!");
            default -> notEnoughArguments("/2fa <vk|discord> \n - Переключает режим двух-факторной авторизации.");
        }
    }
}
