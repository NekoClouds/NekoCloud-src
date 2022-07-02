package me.nekocloud.core.api.utils.chat;

import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

@UtilityClass
public class ChatUtil {

    private final Pattern CHAT_COLOR_PATTERN = Pattern.compile("(?i)&([0-9A-F])");
    private final Pattern CHAT_OTHER_PATTERN = Pattern.compile("(?i)&([K-R])");

    public String translateColorCodes(String text) {
        return CHAT_COLOR_PATTERN.matcher(text).replaceAll("§$1");
    }

    public String translateOtherCodes(String text) {
        return CHAT_OTHER_PATTERN.matcher(text).replaceAll("§$1");
    }

}
