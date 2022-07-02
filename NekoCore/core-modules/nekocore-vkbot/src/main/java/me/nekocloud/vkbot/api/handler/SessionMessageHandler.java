package me.nekocloud.vkbot.api.handler;

import org.jetbrains.annotations.NotNull;

public interface SessionMessageHandler {

    /**
     * Вызывается при получении сообщения
     *
     * @param messageBody - сообщение
     */
    void onMessage(@NotNull String messageBody);
}
