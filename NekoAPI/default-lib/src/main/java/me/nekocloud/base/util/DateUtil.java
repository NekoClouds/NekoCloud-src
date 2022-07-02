package me.nekocloud.base.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@UtilityClass
public class DateUtil {

    public static final Date DATE_FORMATTER = new Date();

    public static final String DEFAULT_DATETIME_PATTERN = ("dd.MM.yyyy h:mm:ss a");
    public static final String DEFAULT_DATE_PATTERN     = ("EEE, d MMM, yyyy");
    public static final String DEFAULT_TIME_PATTERN     = ("h:mm a");


    public String formatPattern(final @NotNull String pattern) {
        return createDateFormat(pattern).format(DATE_FORMATTER);
    }

    public String formatTime(final long millis, final @NotNull String pattern) {
        return createDateFormat(pattern).format(new Time(millis));
    }


    @Contract("_ -> new")
    private @NotNull DateFormat createDateFormat(final @NotNull String pattern) {
        return new SimpleDateFormat(pattern);
    }


    @SneakyThrows
    public Date parseDate(@NotNull String datePattern,
                          @NotNull String formattedDate) {

        return createDateFormat(datePattern).parse(formattedDate);
    }

}
