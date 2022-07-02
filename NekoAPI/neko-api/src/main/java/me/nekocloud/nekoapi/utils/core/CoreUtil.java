package me.nekocloud.nekoapi.utils.core;

import lombok.experimental.UtilityClass;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.api.scoreboard.ScoreBoardAPI;

import java.io.File;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class CoreUtil {

    private final GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();

    public String getConfigDirectory() {
        return "/home/nekocl/create/" + GAMER_MANAGER.getSpigot().getName().split("-")[0] + "/config";
    }

    public String getServerDirectory() {
        return "/home/nekocl/servers/" + GAMER_MANAGER.getSpigot().getName();
    }

    public String getGameWorld() {
        String world = null;
        File folder = new File("/home/nekocl/servers/" + GAMER_MANAGER.getSpigot().getName());
        File[] listOfFiles = folder.listFiles();
        assert listOfFiles != null;
        for (File file : listOfFiles) {
            if (file.isDirectory()) {
                if (!file.getName().equals("logs")
                        && !file.getName().equals("lobby")
                        && !file.getName().equals("plugins")
                        && !file.getName().equals("timings")
                        && !file.getName().equals("endlobby")) {
                    world = file.getName();
                }
            }
        }
        return world;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
}
