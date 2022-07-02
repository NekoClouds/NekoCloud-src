package me.nekocloud.core.api.utils.redirect;

import lombok.experimental.UtilityClass;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.connection.server.Bukkit;

import java.util.Comparator;
import java.util.regex.Pattern;

@UtilityClass
public class RedirectUtil {

    private final NekoCore CORE = NekoCore.getInstance();
    private final Pattern LOBBY_PATTERN = Pattern.compile("^(hub-[0-9])+$");

    public Bukkit getRandomHub(){
        return getRandomServer(LOBBY_PATTERN);
    }

    public Bukkit getRandomServer(final String regex) {
        return getRandomServer(Pattern.compile(regex));
    }

    public Bukkit getRandomServer(final Pattern pattern) {
        return CORE.getBukkitServers().stream()
                .unordered()
                .filter(bukkit -> pattern.matcher(bukkit.getName()).matches())
                .findFirst()
                .orElse(null);
    }

    public Bukkit getDischargedServer(final String regex) {
        return getDischargedServer(Pattern.compile(regex));
    }

    public Bukkit getDischargedServer(final Pattern pattern) {
        return CORE.getBukkitServers().stream()
                .filter(bukkit -> pattern.matcher(bukkit.getName()).matches())
                .min(Comparator.comparing(Bukkit::getOnline))
                .orElse(null);
    }
}

