package pw.novit.nekocloud.bungee.api.utils;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import lombok.val;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;
import java.util.regex.Pattern;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@UtilityClass
public class ChatUtil {

    Pattern CHAT_COLOR_PATTERN = Pattern.compile("(?i)&([0-9A-F])");
    Pattern CHAT_OTHER_PATTERN = Pattern.compile("(?i)&([K-R])");

    public BaseComponent getComponentFromList(@NonNull List<String> list) {
        val size = list.size();

        val components = new TextComponent();
        for (int i = 0; i < size; i++) {
            components.addExtra(list.get(i));
            if (i + 1 < size) {
                components.addExtra("\n");
            }
        }
        return components;
    }

    public String translateColorCodes(String text) {
        return CHAT_COLOR_PATTERN.matcher(text).replaceAll("§$1");
    }

    public String translateOtherCodes(String text) {
        return CHAT_OTHER_PATTERN.matcher(text).replaceAll("§$1");
    }
}
