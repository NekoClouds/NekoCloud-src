package me.nekocloud.nekoapi.utils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class WorldTime {

    public void freezeTime(FreezeData freezeData) {
        TimeTask.TIME_FREEZER_MAP.put(freezeData.getWorldName().toLowerCase(), freezeData);
    }

    public void freezeTime(@NotNull World world, long time, boolean isStorm) {
        freezeTime(new FreezeData(world.getName(), time, isStorm));
    }

    public void freezeTime(String worldName, long time, boolean isStorm) {
        freezeTime(new FreezeData(worldName, time, isStorm));
    }

    public boolean isFrozen(@NotNull String worldName) {
        return TimeTask.TIME_FREEZER_MAP.containsKey(worldName.toLowerCase());
    }

    public boolean isFrozen(World world) {
        return world != null && isFrozen(world.getName());
    }

    public void resumeTime(@NotNull String worldName) {
        TimeTask.TIME_FREEZER_MAP.remove(worldName.toLowerCase());
    }

    public void resumeTime(@NotNull World world) {
        resumeTime(world.getName());
    }

    public static class TimeTask implements Runnable {

        private static final Map<String, FreezeData> TIME_FREEZER_MAP = new HashMap<>();

        @Override
        public void run() {
            for (val freezeData : TIME_FREEZER_MAP.values()) {
                val world = Bukkit.getWorld(freezeData.getWorldName());

                if (world != null) {
                    world.setTime(freezeData.getTime());
                    world.setStorm(freezeData.isStorm());
                }
            }
        }
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @Getter
    @AllArgsConstructor
    public class FreezeData{
        String worldName;
        long time;
        boolean storm;
    }
}
