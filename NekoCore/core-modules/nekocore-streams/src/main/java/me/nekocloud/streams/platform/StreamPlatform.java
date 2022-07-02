package me.nekocloud.streams.platform;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import me.nekocloud.streams.detail.AbstractStreamDetails;
import me.nekocloud.streams.exception.StreamException;
import org.jetbrains.annotations.NotNull;

public interface StreamPlatform<T extends AbstractStreamDetails> {

    //для десериализации
    Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    /**
     * Вернуть новый объект информации о стриме по указанной ссылке на него
     */
    T parseStreamUrl(@NotNull String streamUrl);

    /**
     * Вернуть красивый URL (ну крч просто URL для пользователя)
     */
    String makeBeautifulUrl(@NotNull AbstractStreamDetails streamDetails);

    /**
     * Получить URL для отправки запроса
     */
    JsonObject makeRequest(@NotNull String streamId);

    /**
     * Обновить инфо о стриме в зависимости от полученного ответа
     */
    void updateStreamDetails(@NotNull AbstractStreamDetails streamDetails,
                             @NotNull JsonObject jsonObject) throws StreamException;

    /**
     * Получить отображаемое имя платформы (для игроков)
     */
    String getDisplayName();

}
