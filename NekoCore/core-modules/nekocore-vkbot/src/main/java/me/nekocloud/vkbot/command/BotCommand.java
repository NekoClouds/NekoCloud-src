package me.nekocloud.vkbot.command;

import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.TimeUtil;
import me.nekocloud.core.common.auth.CoreAuth;
import me.nekocloud.vkbot.api.VkApi;
import me.nekocloud.vkbot.api.objects.keyboard.button.KeyboardButtonColor;
import me.nekocloud.vkbot.api.objects.keyboard.button.action.TextButtonAction;
import me.nekocloud.vkbot.api.objects.message.Message;
import me.nekocloud.vkbot.bot.VkBot;
import me.nekocloud.vkbot.user.VkUser;
import org.jetbrains.annotations.NotNull;

public class BotCommand extends VkCommand{

    public BotCommand() {
        super("bot", "nekobot", "бот");
    }

    @Override
    protected void execute(VkUser vkUser, @NotNull Message message, @NotNull String[] args, @NotNull VkBot vkBot) {
        String reply =
                "😺 NekoCloud | VK BOT \n\n" +
                "⚒ Информация: \n" +
                "- Авторизированно пользователей: " + CoreAuth.getAuthManager().getAuthPlayerMap().size() + "\n" +
                "- Загеристрированно команд VK: " + vkBot.getCommandMap().size() + "\n" +
                "- Кэшированно пользователей: " + VkUser.VK_USER_MAP.size() + "\n" +
                "- Бот работает: " + TimeUtil.leftTime(Language.RUSSIAN, VkApi.getSessionMillis()) +
                "\n\n" +
                "Наш IP: mc.NekoCloud.me \n" +
                "Магазин: NekoCloud.me";

        new Message()
                .peerId(message.getPeerId())
                .body(reply)

                .keyboard(true, true)
                .button(KeyboardButtonColor.POSITIVE, 0, new TextButtonAction("/команды ", "Команды бота"))
                .button(KeyboardButtonColor.PRIMARY, 0, new TextButtonAction("/mod reload vkbot", "Перезапуск бота VK"))
                .message()

                .send(vkBot);
    }
}
