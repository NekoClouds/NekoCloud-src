package me.nekocloud.vkbot.api.objects.message;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import me.nekocloud.core.api.http.MultipartUtility;
import me.nekocloud.vkbot.api.VkApi;
import me.nekocloud.vkbot.api.callback.ResponseCallback;
import me.nekocloud.vkbot.api.objects.keyboard.Keyboard;
import me.nekocloud.vkbot.api.objects.keyboard.button.KeyboardButton;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Message object
 */
@Log4j2
public class Message {

    /**
     * Peer ID prefix for chats
     */
    public static final int CHAT_PREFIX = 2000000000;

    @Getter
    private int messageId, conversationMessageId, userId, peerId;

    @Getter
    private String body;

    private Map<String, String> payload = new HashMap<>();

    @Setter
    private List<Message> forwardedMessages = new ArrayList<>();

    @Setter
    private List<String> attachments = new ArrayList<>();

    private List<BufferedImage> imagesToUpload = new ArrayList<>();

    private Keyboard keyboard;

    public Message conversationMessageId(int conversationMessageId) {
        this.conversationMessageId = conversationMessageId;
        return this;
    }

    public Message messageId(int messageId) {
        this.messageId = messageId;
        return this;
    }

    public Message userId(int userId) {
        this.userId = userId;
        return this;
    }

    public Message peerId(int peerId) {
        this.peerId = peerId;
        return this;
    }

    public Message body(@NotNull String body) {
        this.body = body;
        return this;
    }

    public Message response() {
        return new Message().peerId(peerId);
    }

    public Message payload(@NotNull String payloadString) {
        JsonElement element = VkApi.GSON.fromJson(payloadString, JsonElement.class);

        for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
            this.payload.put(entry.getKey(), VkApi.toString(entry.getValue()));
        }

        return this;
    }

    public Message attachments(@NotNull String... attachments) {
        this.attachments.addAll(Arrays.asList(attachments));
        return this;
    }

    public Message forwardedMessages(@NotNull Message... message) {
        this.forwardedMessages.addAll(Arrays.asList(message));
        return this;
    }

    public Message photos(@NotNull BufferedImage... images) {
        this.imagesToUpload.addAll(Arrays.asList(images));
        return this;
    }

    public Keyboard keyboard(boolean onetime, boolean inline) {
        this.keyboard = new Keyboard(this, onetime, inline);
        return keyboard;
    }

    public Keyboard keyboard() {
        return keyboard;
    }

    public boolean isFromChat() {
        return getChatId() > 0;
    }

    public List<Message> getForwardedMessages() {
        return Collections.unmodifiableList(forwardedMessages);
    }

    public List<String> getAttachments() {
        return Collections.unmodifiableList(attachments);
    }

    public List<BufferedImage> getImagesToUpload() {
        return Collections.unmodifiableList(imagesToUpload);
    }

    public int getChatId() {
        return peerId - CHAT_PREFIX;
    }

    public String getPayloadValue(@NotNull String key) {
        return payload == null ? null : payload.get(key);
    }

    public void send(@NotNull VkApi vkApi) {
        send(vkApi, ResponseCallback.EMPTY_CALLBACK);
    }

    public void send(@NotNull VkApi vkApi, @NotNull ResponseCallback callback) {
        if (imagesToUpload.size() > 0) {
            uploadPhotosAsync(vkApi, () -> send(vkApi, callback));
            return;
        }

        JsonArray attachments = new JsonArray();
        this.attachments.forEach(attachments::add);

        JsonArray forwardedMessages = new JsonArray();
        this.forwardedMessages.forEach(message -> forwardedMessages.add(message.getMessageId()));

        JsonObject params = new JsonObject();

        params.addProperty("peer_id", peerId);
        params.addProperty("random_id", 0);
        params.addProperty("message", body);
        params.add("attachment", attachments);
        params.add("forward_messages", forwardedMessages);

        if (keyboard != null) {
            JsonObject keyboard = new JsonObject();

            if (this.keyboard.isInline()) {
                keyboard.addProperty("inline", true);
            } else {
                keyboard.addProperty("one_time", this.keyboard.isOneTime());
            }

            JsonArray buttons = new JsonArray();

            int maximumSections = 0;

            for (int sectionNumber : this.keyboard.getButtons().keySet()) {
                if (sectionNumber > maximumSections) {
                    maximumSections = sectionNumber;
                }
            }

            for (int i = 0; i <= maximumSections; i++) {
                JsonArray array = new JsonArray();

                List<KeyboardButton> buttonList = this.keyboard.getButtons().get(i);

                if (buttonList == null) {
                    continue;
                }

                for (KeyboardButton button : buttonList) {
                    array.add(button.toJsonObject());
                }

                buttons.add(array);
            }

            keyboard.add("buttons", buttons);

            params.add("keyboard", keyboard);
        }

        vkApi.call("messages.send", params, callback);
    }

    private void uploadPhotosAsync(@NotNull VkApi vkApi, @NotNull Runnable runnable) {
        VkApi.EXECUTOR_SERVICE.submit(() -> {
            ResponseCallback saveCallback = new ResponseCallback() {
                @Override
                public void onResponse(String result) {
                    JsonElement resultElement = VkApi.GSON.fromJson(result, JsonElement.class);

                    JsonObject object = resultElement.getAsJsonObject();

                    if (!object.has("response")) {
                        return;
                    }

                    JsonArray response = object.getAsJsonArray("response");

                    val firstObject = response.get(0).getAsJsonObject();

                    val photoAsAttach = "photo" + firstObject.get("owner_id").getAsInt() + "_" + firstObject.get("id").getAsInt();

                    attachments.add(photoAsAttach);
                }

                @Override
                public void onException(Exception exception) {
                    System.out.println("Error occured whilst saving photo: " + exception.getMessage());
                }
            };

            for (BufferedImage image : getImagesToUpload()) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                try {
                    ImageIO.write(image, "png", outputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                byte[] bytes = outputStream.toByteArray();

                JsonObject uploadParams = new JsonObject();
                uploadParams.addProperty("peer_id", peerId);

                ResponseCallback uploadCallback = new ResponseCallback() {
                    @Override
                    public void onResponse(String result) {
                        JsonElement resultElement = VkApi.GSON.fromJson(result, JsonElement.class);

                        JsonObject object = resultElement.getAsJsonObject();

                        if (!object.has("response")) {
                            return;
                        }

                        JsonObject responseObject = object.getAsJsonObject("response");

                        log.info(responseObject.getAsJsonObject("upload_url"));
                        if (!responseObject.has("upload_url")) {
                            return;
                        }

                        String url = responseObject.get("upload_url").getAsString();

                        MultipartUtility multipartUtility = new MultipartUtility(url);
                        multipartUtility.addBytesPart("photo", "photo.png", bytes);

                        String uploadingOfPhotoResponseString = null;

                        try {
                            uploadingOfPhotoResponseString = multipartUtility.finish();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (uploadingOfPhotoResponseString == null) {
                            return;
                        }

                        JsonElement uploadElement = VkApi.GSON.fromJson(uploadingOfPhotoResponseString, JsonElement.class);

                        JsonObject uploadObject = uploadElement.getAsJsonObject();

                        if (!uploadObject.has("server")
                                || !uploadObject.has("photo")
                                || !uploadObject.has("hash")) {

                            return;
                        }

                        JsonObject saveParams = new JsonObject();

                        saveParams.addProperty("server", uploadObject.get("server").getAsInt());
                        saveParams.addProperty("photo", uploadObject.get("photo").getAsString());
                        saveParams.addProperty("hash", uploadObject.get("hash").getAsString());

                        vkApi.callSync("photos.saveMessagesPhoto", saveParams, saveCallback);
                    }

                    @Override
                    public void onException(Exception exception) {
                        System.out.println("Can not upload photo to vk servers: " + exception.getMessage());
                    }
                };

                vkApi.callSync("photos.getMessagesUploadServer", uploadParams, uploadCallback);
            }

            imagesToUpload.clear();

            runnable.run();
        });
    }

}