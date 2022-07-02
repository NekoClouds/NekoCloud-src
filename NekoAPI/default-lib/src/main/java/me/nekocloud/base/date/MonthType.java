package me.nekocloud.base.date;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.nekocloud.base.locale.Language;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

@AllArgsConstructor
@Getter
public enum MonthType {
    JANUARY(0),
    FEBRUARY(1),
    MARCH(2),
    APRIL(3),
    MAY(4),
    JUNE(5),
    JULY(6),
    AUGUST(7),
    SEPTEMBER(8),
    OCTOBER(9),
    NOVEMBER(10),
    DECEMBER(11);

    private final int id;

    public @NotNull String getName(@NotNull Language language) {
        return language.getMessage(this.name());
    }

    private static final Int2ObjectMap<MonthType> MONTHS = new Int2ObjectOpenHashMap<>();

    /**
     * получить месяц который сейчас
     * @return - месяц
     */
    public static MonthType getMonth() {
        return MONTHS.get(Calendar.getInstance().get(Calendar.MONTH));
    }

    public static MonthType getMonth(@NotNull Calendar calendar) {
        return MONTHS.get(calendar.get(Calendar.MONTH));
    }

    static {
        for (final MonthType monthType : values()) {
            MONTHS.put(monthType.id, monthType);
        }
    }
}
