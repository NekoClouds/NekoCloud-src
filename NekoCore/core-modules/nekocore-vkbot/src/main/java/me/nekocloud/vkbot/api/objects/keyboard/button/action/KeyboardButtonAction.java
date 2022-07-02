package me.nekocloud.vkbot.api.objects.keyboard.button.action;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class KeyboardButtonAction {

    private final String payload;

    protected abstract JsonObject toJsonObject();

    public JsonObject convertToJson() {
        JsonObject action = toJsonObject();

        JsonObject payload = new JsonObject();
        payload.addProperty("payload", this.payload);

        action.add("payload", payload);
        return action;
    }

}
