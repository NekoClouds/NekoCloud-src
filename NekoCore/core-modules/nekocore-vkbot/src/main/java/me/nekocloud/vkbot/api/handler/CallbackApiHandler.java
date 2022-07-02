package me.nekocloud.vkbot.api.handler;

import me.nekocloud.vkbot.api.objects.message.Message;

public interface CallbackApiHandler {

    /**
     * Вызывается при получении сообщения
     *
     * @param message - сообщение
     */
    void onMessage(Message message);

    /**
     * Вызывается при приглашении в беседу пользователя
     *
     * @param chatId    - id чата вконтакте
     * @param inviteId  - id того, кто пригласил пользователя
     * @param invitedId - id того, кого пригласили
     */
    void onChatUserInvite(int chatId, int inviteId, int invitedId);

    /**
     * Вызывается при входе пользователем в беседу по ссылке
     *
     * @param chatId - id чата вконтакте
     * @param userId - id того, кто присоединился
     */
    void onChatUserJoin(int chatId, int userId);

    /**
     * Вызывается при исключении из беседы пользователя
     *
     * @param chatId   - id чата вконтакте
     * @param kickId   - id того, кто исключил пользователя
     * @param kickedId - id того, кого исключили
     */
    void onChatUserKick(int chatId, int kickId, int kickedId);

    /**
     * Вызывается при измении заголовка беседы
     *
     * @param chatId - id чата вконтакте
     * @param userId - id пользователя изменившего сообщение
     */
    void onChatTitleChange(int chatId, int userId, String newTitle);


}
