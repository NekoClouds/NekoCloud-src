package me.nekocloud.vkbot.api.objects.keyboard.button.action;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public class LocationButtonAction extends KeyboardButtonAction {

    public LocationButtonAction(@NotNull String payload) {
        super(payload);
    }

    @Override
    protected JsonObject toJsonObject() {
        JsonObject params = new JsonObject();

        params.addProperty("type", "location");

        return params;
    }
}
