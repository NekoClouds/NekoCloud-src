package me.nekocloud.base.util;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.io.Reader;

@UtilityClass
public class GsonUtil {

    @Getter
    private final Gson gson = new Gson();

    /**
     * Преобразовать объект в JSON
     *
     * @param object - объект
     */
    public String toJson(final @NotNull Object object) {
        return gson.toJson(object);
    }

    /**
     * Преобразовать JSON обратно в объект
     *
     * @param json - JSON
     * @param clazz - класс объекта
     */
    public <T> T fromJson(final @NotNull String json, final Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    public <T> T fromJson(final @NotNull Reader reader, final Class<T> clazz) {
        return gson.fromJson(reader, clazz);
    }
}
