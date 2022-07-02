package me.nekocloud.nekoapi.tops;

import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.TimeUtil;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Supplier;

public final class HologramAnimation implements Supplier<String> {

    private final Language language;
    private final int timeMinutes;

    private LocalDateTime nextTime;

    public HologramAnimation(final Language language, final int timeMinutes) {
        this.language = language;
        this.timeMinutes = timeMinutes;

        nextTime = LocalDateTime.now().plusMinutes(timeMinutes);
    }

    @Override
    public @NotNull String get() {
        final Duration between = Duration.between(LocalDateTime.now(), nextTime);
        if (between.isNegative() || between.isZero()) {
            nextTime = LocalDateTime.now().plusMinutes(timeMinutes);
            return language.getMessage("HOLO_TOP_UPDATE_NOW");
        }

        final int millis = (int) between.toMillis();
        return language.getMessage("HOLO_TOP_UPDATE", TimeUtil.leftTime(language, millis));
    }
}
