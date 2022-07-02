package me.nekocloud.base.skin.response;

import com.google.gson.*;

import java.lang.reflect.Type;

public final class SkinSerializer implements JsonDeserializer<SkinProperty> {

    @Override
    public SkinProperty deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (!(json instanceof JsonArray)) {
            return null;
        }
        for (JsonElement jsonElement : (JsonArray)json) {
            JsonObject jsonObject;

            if (!(jsonElement instanceof JsonObject) ||
                    !(jsonObject = (JsonObject)jsonElement).has("signature")) continue;

            String name = jsonObject.getAsJsonPrimitive("name").getAsString();
            String value = jsonObject.getAsJsonPrimitive("value").getAsString();
            String signature = jsonObject.getAsJsonPrimitive("signature").getAsString();
            return new SkinProperty(name, value, signature);
        }
        return null;
    }
}
