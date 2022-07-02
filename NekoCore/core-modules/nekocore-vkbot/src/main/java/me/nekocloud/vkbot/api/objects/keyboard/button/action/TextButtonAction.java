package me.nekocloud.vkbot.api.objects.keyboard.button.action;

import com.google.gson.JsonObject;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class TextButtonAction extends KeyboardButtonAction {

    private final String label;

    public TextButtonAction(@NotNull String payload, @NotNull String label) {
        super(payload);

        this.label = label;
    }

    @Override
    public JsonObject toJsonObject() {
        JsonObject params = new JsonObject();

        params.addProperty("type", "text");
        params.addProperty("label", label);

        return params;
    }
}
