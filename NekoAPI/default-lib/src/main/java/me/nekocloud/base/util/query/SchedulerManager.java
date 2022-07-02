package me.nekocloud.base.util.query;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class SchedulerManager {

    Map<String, ScheduledFuture<?>> schedulerMap = new HashMap<>();
    ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

    /**
     * Запустить асинхронный поток
     * @param command - команда потока
     */
    public void runAsync(final Runnable command) {
        scheduledExecutor.submit(command);
    }

    /**
     * Отменить и закрыть поток шедулера
     * по его ID
     *
     * @param schedulerId - ID шедулера
     */
    public void cancelScheduler(final @NotNull String schedulerId) {
        final ScheduledFuture<?> scheduledFuture = schedulerMap.get(schedulerId.toLowerCase());

        if ( scheduledFuture == null || scheduledFuture.isCancelled() ) {
            return;
        }

        scheduledFuture.cancel(true);
        schedulerMap.remove(schedulerId.toLowerCase());
    }

    /**
     * Воспроизвести команду Runnable через
     * определенное количество времени
     *
     * @param schedulerId - ID шедулера
     * @param command - команда
     * @param delay - время
     * @param timeUnit - единица времени
     */
    public void runLater(
            final String schedulerId,
            final Runnable command,
            final long delay,
            final TimeUnit timeUnit
    ) {
        final ScheduledFuture<?> scheduledFuture = scheduledExecutor.schedule(command, delay, timeUnit);

        schedulerMap.put(schedulerId.toLowerCase(), scheduledFuture);
    }

    /**
     * Воспроизвести команду Runnable через
     * определенное количество времени циклично
     *
     * @param schedulerId - ID шедулера
     * @param command - команда
     * @param delay - время
     * @param period - период цикличного воспроизведения
     * @param timeUnit - единица времени
     */
    public void runTimer(
            final @NotNull String schedulerId,
            final Runnable command,
            final long delay, long period,
            final TimeUnit timeUnit
    ) {
        final ScheduledFuture<?> scheduledFuture
                = scheduledExecutor.scheduleAtFixedRate(command, delay, period, timeUnit);

        schedulerMap.put(schedulerId.toLowerCase(), scheduledFuture);
    }


}