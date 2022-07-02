package me.nekocloud.vkbot.command.feature;

import me.nekocloud.vkbot.api.objects.keyboard.button.KeyboardButtonColor;
import me.nekocloud.vkbot.api.objects.keyboard.button.action.TextButtonAction;
import me.nekocloud.vkbot.api.objects.message.Message;
import me.nekocloud.vkbot.bot.VkBot;
import me.nekocloud.vkbot.command.VkCommand;
import me.nekocloud.vkbot.user.VkUser;
import org.jetbrains.annotations.NotNull;

public class DelKeyboardCommand extends VkCommand {

    public DelKeyboardCommand() {
        super("keyboard");
    }

    @Override
    protected void execute(VkUser vkUser, @NotNull Message message, @NotNull String[] args, @NotNull VkBot vkBot) {

        new Message()
                .peerId(message.getPeerId())
                .body("Убиралка клавы включена")

                .keyboard(true, false)
                .button(KeyboardButtonColor.PRIMARY, 0, new TextButtonAction(" ", "Убрать клавиатуру"))
                .message()

                .send(vkBot);

    }
}
