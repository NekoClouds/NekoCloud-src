package me.nekocloud.core.api.scheduler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.nekocloud.core.NekoCore;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public abstract class CommonScheduler implements Runnable {

    @Getter
    private final String identifier;

    /**
     * Отмена и закрытие потока
     */
    public void cancel() {
        NekoCore.getInstance().getSchedulerManager().cancelScheduler(identifier);
    }

    /**
     * Запустить асинхронный поток
     */
    public void runAsync() {
        NekoCore.getInstance().getSchedulerManager().runAsync(this);
    }

    /**
     * Запустить поток через определенное
     * количество времени
     *
     * @param delay - время
     * @param timeUnit - единица времени
     */
    public void runLater(long delay, TimeUnit timeUnit) {
        NekoCore.getInstance().getSchedulerManager().runLater(identifier, this, delay, timeUnit);
    }

    /**
     * Запустить цикличный поток через
     * определенное количество времени
     *
     * @param delay - время
     * @param period - период цикличного воспроизведения
     * @param timeUnit - единица времени
     */
    public void runTimer(long delay, long period, TimeUnit timeUnit) {
        NekoCore.getInstance().getSchedulerManager().runTimer(identifier, this, delay, period, timeUnit);
    }

}
