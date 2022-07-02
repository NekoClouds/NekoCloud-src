package me.nekocloud.base.util;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.experimental.UtilityClass;
import me.nekocloud.base.locale.Language;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public final class TimeUtil {

    public final Pattern TIME_TO_MILLIS_PATTERN = Pattern.compile("(?i)" + "(\\d{1,3}(?=ns))?"
                    + "(\\d{1,3}(?=mc))?" + "(\\d{1,3}(?=ms))?"
                    + "(\\d{1,3}(?=s))?" + "(\\d{1,3}(?=m))?"
                    + "(\\d{1,3}(?=h))?" + "(\\d{1,3}(?=d))?"
                    + "(\\d{1,3}(?=w))?"+ "(\\d{1,3}(?=y))?");

    public static Month getTotalMonth() {
        return LocalDateTime.now().getMonth();
    }

    public static @NotNull String getMonthName(@NotNull Month month, @NotNull Language language) {
        return language.getMessage(month.name());
    }

    public String leftTime(Language language, long unixTime, boolean future) {
        if (unixTime < 0L)
            return language.getMessage("ETERNITY");

        long seconds;

        if(!future) {
            seconds = System.currentTimeMillis() - unixTime;
        } else {
            seconds = unixTime - System.currentTimeMillis();
        }

        seconds /= 1000L;
        seconds = seconds + 1; //FIX

        if (seconds < 0)
            return language.getMessage("UNKNOWN");

        long minutes = 0L;
        long hours = 0L;
        long days = 0L;
        long i;

        if (seconds >= 60L) {
            i = (long) ((int) (seconds / 60L));
            minutes = i;
            seconds %= 60L;
        }

        if (minutes >= 60L) {
            i = (long) ((int) (minutes / 60L));
            hours = i;
            minutes %= 60L;
        }

        if (hours >= 24L) {
            i = (long) ((int) (hours / 24L));
            days = i;
            hours %= 24L;
        }

        List<String> day = language.getList("TIME_DAY_1");
        List<String> hour = language.getList("TIME_HOURS_1");
        List<String> min = language.getList("TIME_MINUTES_1");
        List<String> sec = language.getList("TIME_SECOND_2");

        String s = "", msg;
        if(days > 0L) {
            msg = formatTime(days, day.get(0), day.get(1), day.get(2), day.get(3));
            s = s + days + " " + msg + " ";
        }

        if(hours > 0L) {
            msg = formatTime(hours, hour.get(0), hour.get(1), hour.get(2), hour.get(3));
            s = s + hours + " " + msg + " ";
        }

        if(minutes > 0L) {
            msg = formatTime(minutes, min.get(0), min.get(1), min.get(2), min.get(3));
            s = s + minutes + " " + msg + " ";
        }

        if(seconds > 0L) {
            msg = formatTime(seconds, sec.get(0), sec.get(1), sec.get(2), sec.get(3));
            s = s + seconds + " " + msg + " ";
        }

        if (s.length() > 0)
            s = s.substring(0, s.length() - 1);

        return s;

    }

    public static String leftTime(Language language, long millis, String keySecond, String keyMinutes, String keyHours, String keyDays) {
        String msg;
        long i;
        if (millis < 0L) {
            return language.getMessage("ETERNITY");
        }
        long seconds = millis / 1000L;
        long minutes = 0L;
        long hours = 0L;
        long days = 0L;
        if (seconds >= 60L) {
            minutes = ((int)(seconds / 60L));
            seconds %= 60L;
        }

        if (minutes >= 60L) {
            hours = ((int)(minutes / 60L));
            minutes %= 60L;
        }

        if (hours >= 24L) {
            days = ((int)(hours / 24L));
            hours %= 24L;
        }

        List<String> day = language.getList(keyDays);
        List<String> hour = language.getList(keyHours);
        List<String> min2 = language.getList(keyMinutes);
        List<String> sec = language.getList(keySecond);

        String s2 = "";

        if (days > 0L) {
            msg = TimeUtil.formatTime(days, day.get(0), day.get(1), day.get(2), day.get(3));
            s2 = s2 + days + " " + msg + " ";
        }
        if (hours > 0L) {
            msg = TimeUtil.formatTime(hours, hour.get(0), hour.get(1), hour.get(2), hour.get(3));
            s2 = s2 + hours + " " + msg + " ";
        }
        if (minutes > 0L) {
            msg = TimeUtil.formatTime(minutes, min2.get(0), min2.get(1), min2.get(2), min2.get(3));
            s2 = s2 + minutes + " " + msg + " ";
        }
        if (seconds > 0L) {
            msg = TimeUtil.formatTime(seconds, sec.get(0), sec.get(1), sec.get(2), sec.get(3));
            s2 = s2 + seconds + " " + msg + " ";
        }
        if (s2.length() > 0) {
            s2 = s2.substring(0, s2.length() - 1);
        }
        return s2;
    }

    public static String leftTime(Language language, long millis) {
        return TimeUtil.leftTime(language, millis, "TIME_SECOND_2", "TIME_MINUTES_1", "TIME_HOURS_1", "TIME_DAY_1");
    }


    private String formatTime(long num, String main, String single, String lessFive, String others) {
        int end = (int)(num % 10L);
        if(num % 100L <= 10L || num % 100L >= 15L) {
            switch(end) {
            case 1:
                return main + single;
            case 2:
            case 3:
            case 4:
                return main + lessFive;
            }
        }

        return main + others;
    }

    /**
     * Парсит такие значения, как 5d, 3m, 50s и т.д.
     * Спиздил у GitCoder
     *
     * @param time   - значения, которое нужно парсить
     * @param unitTo - в какую единицу измерения времени парсить
     * @author GitCoder
     */
    public long parseTimeToMillis(@NotNull final String time, @NotNull final TimeUnit unitTo) {
        if (time.startsWith("-a") || time.startsWith("-e"))
            return -1;

        Matcher matcher = TIME_TO_MILLIS_PATTERN.matcher(time);
        Object2IntMap<TimeUnit> values = new Object2IntOpenHashMap<>();

        while (matcher.find()) {
            for (int i = 1; i <= 7; i++) {
                String value = matcher.group(i);

                if (value == null || value.isEmpty()) {
                    continue;
                }

                TimeUnit unit = TimeUnit.values()[i - 1];
                int intValue = Integer.parseInt(value);

                values.put(unit, intValue);
                break;
            }
        }

        if (values.isEmpty()) {
            throw new IllegalArgumentException("Illegal Date");
        }

        AtomicLong total = new AtomicLong();

        values.forEach((timeUnit, value) -> {
            total.addAndGet(unitTo.convert(value, timeUnit));
        });

        if (total.get() <= 0) {
            throw new IllegalArgumentException("Illegal Date");
        }

        return total.get();
    }
}