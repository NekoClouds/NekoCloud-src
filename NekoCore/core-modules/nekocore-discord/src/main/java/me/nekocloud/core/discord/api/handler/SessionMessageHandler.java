package me.nekocloud.core.discord.api.handler;

import org.jetbrains.annotations.NotNull;

public interface SessionMessageHandler {

    /**
     * Вызывается при получении сообщения
     *
     * @param messageBody - сообщение
     */
    void onMessage(@NotNull String messageBody);
}
