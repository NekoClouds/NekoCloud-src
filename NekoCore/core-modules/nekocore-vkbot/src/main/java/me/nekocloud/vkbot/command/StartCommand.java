package me.nekocloud.vkbot.command;

import lombok.val;
import me.nekocloud.base.locale.Language;
import me.nekocloud.vkbot.api.objects.keyboard.button.KeyboardButtonColor;
import me.nekocloud.vkbot.api.objects.keyboard.button.action.CallbackButtonAction;
import me.nekocloud.vkbot.api.objects.keyboard.button.action.TextButtonAction;
import me.nekocloud.vkbot.api.objects.message.Message;
import me.nekocloud.vkbot.bot.VkBot;
import me.nekocloud.vkbot.user.VkUser;
import org.jetbrains.annotations.NotNull;

public class StartCommand extends VkCommand{

    public StartCommand() {
        super("Начать", "Start");

        setOnlyPrivateMessages(true);

        // Разрешение на чтение в чатах
        setListenOnChats(false);
    }

    @Override
    protected void execute(VkUser vkUser, @NotNull Message message, @NotNull String[] args, @NotNull VkBot vkBot) {
        val reply = """
                        😺 NekoCloud | VK-BOT\s

                         Привет! 😸 Я VK-Бот проекта NekoCloud.\s
                         Для использования моих команд тебе нужно\s
                         привязать свой игровой аккаунт ко мне!\s

                        ❗ Нажми на одну из кнопок, чтобы получить помощь по поим командам.\s

                        Наш IP: mc.NekoCloud.me\s
                        Магазин: NekoCloud.me""";

        new Message()
                .peerId(message.getPeerId())
                .body(reply)

                .keyboard(true, true)
                .button(KeyboardButtonColor.PRIMARY, 0, new TextButtonAction("/помощь link ", "Помощь по привязке"))
                .button(KeyboardButtonColor.PRIMARY, 0, new TextButtonAction("/команды", "Доступные команды"))
                .message()

                .send(vkBot);
    }
}
