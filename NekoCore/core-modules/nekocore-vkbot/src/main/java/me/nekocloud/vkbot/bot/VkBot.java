package me.nekocloud.vkbot.bot;

import com.google.common.base.Joiner;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import me.nekocloud.vkbot.api.VkApi;
import me.nekocloud.vkbot.api.callback.ResponseCallback;
import me.nekocloud.vkbot.command.VkCommand;
import me.nekocloud.vkbot.handler.BotCallbackApiHandler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Log4j2
public class VkBot extends VkApi {

    public static final VkBot INSTANCE = new VkBot();


    /**
     * Зарегистрированные команды
     */
    @Getter
    private final Map<String, VkCommand> commandMap     = new HashMap<>();
    private final JsonParser jsonParser                 = new JsonParser();

    public VkBot() {
        addCallbackApiHandler(new BotCallbackApiHandler(this));
    }

    public void printMessage(int peerId, @NotNull String message) {
        val parameters = new JsonObject();

        parameters.addProperty("peer_id", peerId);
        parameters.addProperty("random_id", 0);
        parameters.addProperty("message", message);

        call("messages.send", parameters);
    }

    public void replyMessage(int peerId, @NotNull String message) {
        val parameters = new JsonObject();

        parameters.addProperty("peer_id", peerId);
        parameters.addProperty("reply_to", peerId);
        parameters.addProperty("random_id", 0);
        parameters.addProperty("message", message);

        call("messages.send", parameters);
    }

    public void printAndDeleteMessage(int peerId, @NotNull String message) {
        val parameters = new JsonObject();

        parameters.addProperty("peer_id", peerId);
        parameters.addProperty("random_id", 0);
        parameters.addProperty("message", message);

        call("messages.send", parameters, new ResponseCallback() {
            @Override
            public void onResponse(String result) {
                val object = jsonParser.parse(result).getAsJsonObject();

                deleteMessages(object.get("response").getAsInt());
            }

            @Override
            public void onException(Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    public void deleteMessages(Integer... messageIds) {
        val parameters = new JsonObject();

        parameters.addProperty("message_ids", Joiner.on(",").join(messageIds));

        call("messages.delete", parameters);
    }

    public void kick(int chatId, int userId) {
        val parameters = new JsonObject();

        parameters.addProperty("chat_id", chatId);
        parameters.addProperty("user_id", userId);

        call("messages.removeChatUser", parameters);
    }

    public void editTitle(int chatId, @NotNull String title) {
        val parameters = new JsonObject();

        parameters.addProperty("chat_id", chatId);
        parameters.addProperty("title", title);

        call("messages.editChat", parameters);
    }

    /**
     * Заегистрировать команду
     * @param vkCommand класс
     */
    public void registerCommand(@NotNull VkCommand vkCommand) {
        //регаем все алиасы
        for (val alias : vkCommand.getAliases()) {
            commandMap.put(alias.toLowerCase(), vkCommand);
        }

    }

    public VkCommand getCommand(@NotNull String commandAlias) {
        return commandMap.get(commandAlias.toLowerCase());
    }

}
