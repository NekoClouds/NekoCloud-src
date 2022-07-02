package me.nekocloud.streams.detail;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.nekocloud.streams.platform.StreamPlatform;

@RequiredArgsConstructor
@Getter
@Setter
@ToString
public abstract class AbstractStreamDetails {

    /**
     * Название платформы: Twitch, YouTube
     */
    private final StreamPlatform<?> streamPlatform;

    /**
     * Заголовок стрима
     */
    private String title;

    /**
     * Количество зрителей на стриме
     */
    private int viewers;

    /**
     * Дата начала стрима, дата последнего обновления информации о стриме
     */
    private long startedAtServiceTime, startedAtServerTime = System.currentTimeMillis(), lastUpdateTime;

    /**
     * Получить ID стрима
     */
    public abstract String getIdentity();

    public boolean shouldUpdate() {
        //инфу о стриме нужно обновить, если дата последнего обновления более 60 секунд назад
        return System.currentTimeMillis() - lastUpdateTime > 60_000;
    }

    public boolean isActual() {
        return title != null;
    }

}
