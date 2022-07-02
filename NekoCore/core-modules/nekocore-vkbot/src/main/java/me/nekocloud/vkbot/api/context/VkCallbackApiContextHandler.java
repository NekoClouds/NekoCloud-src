package me.nekocloud.vkbot.api.context;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.val;
import me.nekocloud.base.util.GsonUtil;
import me.nekocloud.vkbot.api.VkApi;
import me.nekocloud.vkbot.api.handler.CallbackApiHandler;
import me.nekocloud.vkbot.api.objects.message.Message;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VkCallbackApiContextHandler implements HttpHandler {

    private static final byte[] OK_BODY = "OK".getBytes();

    private final VkApi vkApi;

    public VkCallbackApiContextHandler(VkApi vkApi) {
        this.vkApi = vkApi;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Preconditions.checkArgument(httpExchange.getRequestMethod().equals("POST"), "POST only allowed");
        Preconditions.checkArgument(httpExchange.getRequestHeaders().getFirst("content-type").equals("application/json"), "json body only allowed");

        JsonObject data;
        JsonObject object = GsonUtil.getJsonManager().fromJson(new InputStreamReader(httpExchange.getRequestBody()), JsonObject.class);
        data = object.getAsJsonObject("object");

        String type = object.get("type").getAsString();

        OutputStream outputStream = httpExchange.getResponseBody();

        if (type.equals("confirmation")) {
            if (vkApi.getConfirmationCode() != null) {
                byte[] response = vkApi.getConfirmationCode().getBytes();

                httpExchange.sendResponseHeaders(200, response.length);

                outputStream.write(response);
                outputStream.close();
                return;
            }
        }

        httpExchange.sendResponseHeaders(200, OK_BODY.length);

        //мы можем не успеть по таймауту вк, поэтому сразу шлем ОК
        outputStream.write(OK_BODY);
        outputStream.close();

        //шлем нахуй возможно фейковые запросы
        if (object.has("secret")
                && !object.get("secret").getAsString().equals(vkApi.getSecretKey())) {
            
            return;
        }

        if (!"message_new".equals(type)) {
            return;
        }

        // ЕБАТЬ В РОТ ВК АПИ
        // НИ В КОЕМ СЛУЧАЕ НЕ ПЕРЕНОСИТЬ ВЫШЕ! ПОСЫЛАЕТ НАХУЙ!
        val jsonObject = data.getAsJsonObject("message");

        val message = parseMessage(jsonObject);
        val actionType = jsonObject.getAsJsonObject("action");

        if (actionType != null) {
            int fromId = jsonObject.get("from_id").getAsInt();
            int peerId = jsonObject.get("peer_id").getAsInt();

            String action = actionType.get("type").getAsString();

            switch (action) {
                case "chat_invite_user" -> {
                    int userId = actionType.get("member_id").getAsInt();

                    for (val callbackApiHandler : vkApi.getCallbackApiHandlerList()) {
                        callbackApiHandler.onChatUserInvite(peerId - Message.CHAT_PREFIX, fromId, userId);
                    }

                }
                case "chat_invite_user_by_link" -> {
                    for (val callbackApiHandler : vkApi.getCallbackApiHandlerList()) {
                        callbackApiHandler.onChatUserJoin(peerId - Message.CHAT_PREFIX, fromId);
                    }

                }
                case "chat_kick_user" -> {
                    val userId = actionType.get("member_id").getAsInt();

                    for (val callbackApiHandler : vkApi.getCallbackApiHandlerList()) {
                        callbackApiHandler.onChatUserKick(peerId - Message.CHAT_PREFIX, fromId, userId);
                    }

                }
                case "chat_title_update" -> {
                    val title = actionType.get("text").getAsString();

                    for (CallbackApiHandler callbackApiHandler : vkApi.getCallbackApiHandlerList()) {
                        callbackApiHandler.onChatTitleChange(peerId - Message.CHAT_PREFIX, fromId, title);
                    }

                }
                default -> System.out.println("new_message > Action \"" + type + "\" is not supported");
            }
        } else {
            val replyMessage = jsonObject.getAsJsonObject("reply_message");
            if (replyMessage != null) {
                message.setForwardedMessages(Collections.singletonList(parseMessage(replyMessage)));
            } else {
                val forwardedMessages = jsonObject.getAsJsonArray("fwd_messages");

                if (forwardedMessages != null) {
                    List<Message> forwardedMessageList = new ArrayList<>();

                    for (JsonElement jsonElement : forwardedMessages) {
                        forwardedMessageList.add(parseMessage(jsonElement.getAsJsonObject()));
                    }

                    message.setForwardedMessages(forwardedMessageList);
                }

            }

            for (val callbackApiHandler : vkApi.getCallbackApiHandlerList()) {
                callbackApiHandler.onMessage(message);
            }
        }

    }



    private Message parseMessage(JsonObject data) {
        Message message = new Message()
                .messageId(data.get("id").getAsInt())
                .userId(data.get("from_id").getAsInt())
                .peerId(data.get("peer_id").getAsInt())
                .body(data.get("text").getAsString());

        if (data.has("payload")) {
            message.payload(data.get("payload").getAsString());

        }

        if (message.isFromChat()) {
            message.conversationMessageId(data.get("conversation_message_id").getAsInt());
        }

        return message;
    }

}