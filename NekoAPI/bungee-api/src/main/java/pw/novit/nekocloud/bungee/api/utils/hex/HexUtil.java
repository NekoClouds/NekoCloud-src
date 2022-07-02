package pw.novit.nekocloud.bungee.api.utils.hex;

import lombok.NonNull;
import lombok.val;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HexUtil {

    private static final Pattern GRADIENT_PATTERN = Pattern.compile("<(gradient|g)(:#([a-fA-F0-9]){6})+>");
    private static final Pattern HEX_PATTERN = Pattern.compile("<(#[a-fA-F0-9]{6})>");
    private static final Pattern STOPPING_PATTERN = Pattern.compile("<(gradient|g)(:#([a-fA-F0-9]){6})+>|(§[a-fA-F0-9])|<(#[a-fA-F0-9]{6})>");


    public static boolean serverSupportsHex() {
        try {
            ChatColor.of(Color.BLACK);
            return true;
        } catch (NoSuchMethodError ignore) {
            return false;
        }
    }

    public static @NotNull String fromHexString(@NonNull String text, @NonNull Pattern hexPattern) {
        if (serverSupportsHex()) {
            text = parseGradients(text);
            Matcher hexColorMatcher = hexPattern.matcher(text);
            while (hexColorMatcher.find()) {
                val hex = hexColorMatcher.group(1);
                val color = ChatColor.of(hex);

                val before = text.substring(0, hexColorMatcher.start());
                val after = text.substring(hexColorMatcher.end());
                text = before + color + after;
                hexColorMatcher = hexPattern.matcher(text);

            }
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }


    public static @NotNull String fromHexString(@NonNull String text) {
        return fromHexString(text, NHexUtil.HEX_PATTERN);
    }

    private static String parseGradients(@NonNull  String text) {
        List<String> formatCodes = Arrays.asList("§o", "§k", "§l", "§n", "§r", "§m");
        String parsed = text;

        Matcher matcher = GRADIENT_PATTERN.matcher(parsed);
        while (matcher.find()) {
            val parsedGradient = new StringBuilder();

            val match = matcher.group();
            int tagLength = match.startsWith("<gr") ? 10 : 3;

            int indexOfClose = match.indexOf(">");
            val hexContent = match.substring(tagLength, indexOfClose);
            List<Color> hexSteps = Arrays.stream(hexContent.split(":")).map(Color::decode).collect(Collectors.toList());

            int stop = findGradientStop(parsed, matcher.end());
            val content = parsed.substring(matcher.end(), stop);

            String cleanedContent = content;
            for (val code: formatCodes) {
                cleanedContent = cleanedContent.replace(code, "");
            }

            val gradientHelper = new GradientHelper(hexSteps, cleanedContent.length());

            String tempFormat = "";
            for (char c : content.toCharArray()) {
                if (c != '§' && tempFormat != "§") {

                    parsedGradient
                            .append(ChatColor.of(gradientHelper.next()).toString())
                            .append(tempFormat)
                            .append(c);
                } else if (c == '§') {

                    tempFormat = "§";
                } else if (c != '§' && tempFormat.contains("§")) {

                    tempFormat += c;
                }
            }

            val before = parsed.substring(0, matcher.start());
            val after = parsed.substring(stop);
            parsed = before + parsedGradient + after;
            matcher = GRADIENT_PATTERN.matcher(parsed);
        }
        return parsed;
    }

    private static int findGradientStop(@NonNull String content, int searchAfter) {
        val matcher = STOPPING_PATTERN.matcher(content);
        while (matcher.find()) {
            if (matcher.start() > searchAfter)
                return matcher.start();
        }
        return content.length() - 1;
    }
}
