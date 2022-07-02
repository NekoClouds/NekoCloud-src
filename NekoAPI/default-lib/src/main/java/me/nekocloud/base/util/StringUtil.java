package me.nekocloud.base.util;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import lombok.val;
import me.nekocloud.base.locale.Language;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@UtilityClass
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StringUtil {
    String PROGRESS_CURR_COLOR  = "§3";
    String PROGRESS_TOTAL_COLOR = "§8";

    int LINE_LENGTH = 70; //длина строки для StringToCenter
    int BAR_LENGTH = 50;

    static SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    Calendar CALENDAR = Calendar.getInstance();

    TreeMap<Integer, String> ROMAN_MAP = new TreeMap<>();

    static {
        ROMAN_MAP.put(1000, "M");
        ROMAN_MAP.put(900, "CM");
        ROMAN_MAP.put(500, "D");
        ROMAN_MAP.put(400, "CD");
        ROMAN_MAP.put(100, "C");
        ROMAN_MAP.put(90, "XC");
        ROMAN_MAP.put(50, "L");
        ROMAN_MAP.put(40, "XL");
        ROMAN_MAP.put(10, "X");
        ROMAN_MAP.put(9, "IX");
        ROMAN_MAP.put(5, "V");
        ROMAN_MAP.put(4, "IV");
        ROMAN_MAP.put(1, "I");
    }


    public String proccess(String cur) { //фикс белых сообщений
        StringBuilder finalString = new StringBuilder();
        StringBuilder colorCode = new StringBuilder(), templates = new StringBuilder();
        for (int i = 0; i < cur.length();) {
            if (cur.charAt(i) == '§' && i + 1 < cur.length()) {
                colorCode = new StringBuilder();
                while (i + 1 < cur.length() && cur.charAt(i) == '§') {
                    colorCode.append(cur.charAt(i)).append(cur.charAt(i + 1));
                    i += 2;
                }
            }
            if (i >= cur.length())
                break;

            if (cur.charAt(i) == '%') {
                if (i + 1 < cur.length() && cur.charAt(i + 1) == 's') {
                    if (i + 2 >= cur.length() || cur.charAt(i + 2) == ' ') {
                        finalString
                                .append(colorCode)
                                .append(cur.charAt(i))
                                .append(cur.charAt(i + 1));
                        i += 2;
                        continue;
                    }
                }
                templates.append(cur.charAt(i));
                if (templates.length() != 1) {
                    finalString.append(colorCode)
                            .append(templates);
                    templates.setLength(0);
                }
                ++i;
                continue;
            }

            if (cur.charAt(i) != ' ') {
                if (templates.length() != 0)
                    templates.append(cur.charAt(i));
                else
                    finalString.append(colorCode).append(cur.charAt(i));
            } else {
                if (templates.length() != 0) {
                    for (int j = 0; j < templates.length(); ++j)
                        finalString.append(colorCode).append(templates.charAt(j));

                    templates.setLength(0);
                }
                finalString.append(cur.charAt(i));
            }
            ++i;
        }

        return finalString.toString();
    }

    public boolean startsWithIgnoreCase(String string, String prefix) {
        if (string == null || string.length() < prefix.length()) {
            return false;
        }
        return string.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    public String onPercentBar(double currentValue, double total) {
        double length = 50.0D;
        double progress = currentValue / total * length;
        return PROGRESS_CURR_COLOR + StringUtils.repeat("|", (int)progress)
            + PROGRESS_TOTAL_COLOR + StringUtils.repeat("|", (int) (length - progress));
    }

    public int onPercent(int value, int max) {
        return (int)((value * 100.0f) / max);
    }

    public String onPercentString(int value, int max) {
        return onPercent(value, max) + "%";
    }

    public String getCompleteTime(int time) {
        long longVal = new BigDecimal(time).longValue();

        int hours = (int) longVal / 3600;
        int remainder = (int) longVal - hours * 3600;
        int min = remainder / 60;

        remainder = remainder - min * 60;

        int sec = remainder;

        return String.format("%02d:%02d", min, sec);
    }

    public String getUTFNumber(int number){
        return switch (number) {
            case 1 -> "❶";
            case 2 -> "❷";
            case 3 -> "❸";
            case 4 -> "❹";
            case 5 -> "❺";
            case 6 -> "❻";
            case 7 -> "❼";
            case 8 -> "❽";
            case 9 -> "❾";
            case 10 -> "❿";
            default -> "0";
        };
    }

    public String getRomanNumber(int number) {
        int l = ROMAN_MAP.floorKey(number);
        if (number == l) {
            return ROMAN_MAP.get(number);
        }

        return ROMAN_MAP.get(l) + getRomanNumber(number-l);
    }

    public String getFormat(int number, String single, String lessFive, String others) {
        if (number % 100 > 10 && number % 100 < 15) {
            return others;
        }

        return switch (number % 10) {
            case 1 -> single;
            case 2, 3, 4 -> lessFive;
            default -> others;
        };
    }

    @Deprecated
    public String getCorrectWord(int integer, String key, @NotNull Language language) {
        List<String> msg = language.getList(key);

        if (msg.size() < 4) {
            return msg.get(0);
        }

        val single = msg.get(0) + msg.get(1);
        val lessfive = msg.get(0) + msg.get(2);
        val others = msg.get(0) + msg.get(3);

        return getFormat(integer, single, lessfive, others);
    }

    public String getDate() {
        return DATE_FORMAT.format(CALENDAR.getTime());
    }

    public String changeEnding(@NotNull String word, char ending) {
        return word.substring(0, word.length() - 1) + ending;
    }

    public static String stringToCenter(String text) {
        //return StringUtils.center(text, LINE_LENGHT); //если делать через этот метод, то он считаеет неправильно как-то
        if (text != null && text.length() <= LINE_LENGTH) {
            return StringUtils.repeat(" ", (LINE_LENGTH - textLength(text)) / 2) + text;
        }
        return text;
    }

    private int textLength(@NotNull String text) {
        int count = 0;
        char[] array = text.toCharArray();
        for (char symbol : array) {
            if (symbol == '§') {
                count += 2;
            }
        }

        return text.length() - count;
    }

    public List<String> getAnimationTitle(String title, String code1, String code2, int spaces) {
        List<String> toReturn = new ArrayList<>();

        while (spaces >= 0) {
            StringBuilder cur = new StringBuilder();
            for (int i = 0; i < title.length(); ++i) {
                cur.append(title.charAt(i));
                for (int j = 0; j < spaces; ++j) {
                    cur.append(" ");
                }

            }
            toReturn.add(cur.toString());
            --spaces;
        }
        for (int i = 0; i < title.length(); ++i) {
            toReturn.add(code1 + "§l" + title.substring(0, i) + code2 + "§l" + title.charAt(i)
                    + code1 + "§l" + title.substring(i + 1));
        }

        toReturn.add(title);

        return toReturn;
    }

    public List<String> getAnimation(String displayName) {
        List<String> animation = new ArrayList<>();

        val displayLine = displayName + "  ";
        val displayInfoArray = displayLine.toCharArray();
        val displayWorkArray = new char[displayInfoArray.length];
        int slotTextSee = 0;

        for (char sym : displayInfoArray) {
            int slot = displayInfoArray.length - 1;
            for (int g = 0; g < getSizeCharArray(displayWorkArray); g++) {
                val line = new StringBuilder();
                for (int i = 0; i < displayInfoArray.length; i++) {
                    if (displayWorkArray[i] == 0) {
                        if (i == slot) {
                            if (i == slotTextSee) {
                                displayWorkArray[slotTextSee] = sym;
                                line.append(sym);
                                slotTextSee++;
                            } else {
                                line.append("§e§l").append(sym);
                                slot -= 1;
                            }
                        } else {
                            line.append(" ");
                        }
                    } else {
                        line.append(displayWorkArray[i]);
                    }
                }

                animation.add(" §8§l» §6§l" + line);

            }
        }

        return animation;
    }

    @Contract(pure = true)
    private int getSizeCharArray(char @NotNull [] array) {
        int i = 0;
        for (char arr : array) {
            if (arr == 0) {
                i++;
            }
        }
        return i;
    }

    public String getLineCode(int line) {
        val builder = new StringBuilder();

        for (char c : String.valueOf(line).toCharArray()) {
            builder.append("§");
            builder.append(c);
        }

        return builder.toString();
    }

    public String getNumberFormat(int amount) {
        return NumberFormat.getNumberInstance(Locale.US).format(amount);
    }

    public String getNumberFormat(double amount) {
        return NumberFormat.getNumberInstance(Locale.US).format(amount);
    }
}
