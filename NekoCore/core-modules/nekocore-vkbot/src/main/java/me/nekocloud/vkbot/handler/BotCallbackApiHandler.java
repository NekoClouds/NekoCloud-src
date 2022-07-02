package me.nekocloud.vkbot.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import me.nekocloud.base.util.query.AsyncUtil;
import me.nekocloud.vkbot.api.handler.CallbackApiHandler;
import me.nekocloud.vkbot.api.objects.message.Message;
import me.nekocloud.vkbot.bot.VkBot;
import me.nekocloud.vkbot.user.VkUser;

import java.util.Arrays;

@Log4j2
@RequiredArgsConstructor
public class BotCallbackApiHandler implements CallbackApiHandler {

    private final VkBot vkBot;

    @Override
    public void onMessage(Message message) {
        String payloadValue = message.getPayloadValue("payload");

        if (payloadValue == null) {
            payloadValue = message.getBody();
        }

        //проверяем наличие сессий перехвата сообщений
        val botUser = VkUser.getVkUser(message.getPeerId());
        if (botUser.hasPrimaryAccount() && botUser.hasMessageHandlers()) {
            botUser.handleMessageHandlers(payloadValue);
            return;
        }

        // Префикс команды
        if (message.isFromChat() && !payloadValue.startsWith("/"))
            return;

        //получаем аргументы команды
        val commandArray = payloadValue.trim().replaceFirst("!", "")
                .replaceFirst("/", "").split(" ");
        val commandArgs = Arrays.copyOfRange(commandArray, 1, commandArray.length);

        //получаем саму команду в боте, которая должна быть зарегана
        val cmd = vkBot.getCommand(commandArray[0].toLowerCase());

        //если команда не зарегана исполнять нам нечего
        if (cmd == null) {
            return;
        }

//        //нуб-команды в чатах все равно не должны работать
//        if (!payloadValue.startsWith("!") && message.isFromChat()) {
//            return;
//        }

        AsyncUtil.submitAsync(() -> cmd.dispatchCommand(message, commandArgs, vkBot));

    }

    @Override
    public void onChatUserInvite(int chatId, int inviteId, int invitedId) {

    }

    @Override
    public void onChatUserJoin(int chatId, int userId) {

    }

    @Override
    public void onChatUserKick(int chatId, int kickId, int kickedId) {

    }

    @Override
    public void onChatTitleChange(int chatId, int userId, String newTitle) {

    }
}
