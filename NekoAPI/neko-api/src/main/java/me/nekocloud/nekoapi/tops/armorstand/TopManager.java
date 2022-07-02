package me.nekocloud.nekoapi.tops.armorstand;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public final class TopManager {

    private final JavaPlugin javaPlugin;
    private final StandTopSql standTopSql;

    private final StandTopListener standTopListener;

    private final StandPlayerStorage standPlayerStorage;

    private final List<Top> tops = new ArrayList<>();
    private final Map<Top, List<StandTop>> allTops = new ConcurrentHashMap<>();

    /**
     * @param table - таблица с которой мы будем грузить инфу о том,
     *             какой топ у игрока выбран в данный момент (ЭТО НЕ ТАБЛИЦА СО СТАТОЙ)
     */
    public TopManager(@NonNull JavaPlugin javaPlugin, String table) {
        this.javaPlugin = javaPlugin;
        this.standTopSql = new StandTopSql(this, table);
        this.standPlayerStorage = new StandPlayerStorage();
        this.standTopListener = new StandTopListener(this);
    }

    @Contract(pure = true)
    public @NotNull @UnmodifiableView List<Top> getTops() {
        return Collections.unmodifiableList(tops);
    }

    @Contract(pure = true)
    public @NotNull @UnmodifiableView Map<Top, List<StandTop>> getAllTops() {
        return Collections.unmodifiableMap(allTops);
    }

    @Nullable
    public Top getTop(int type) {
        if (tops.size() <= type) {
            return null;
        }
        return tops.get(type);
    }

    @Nullable
    public Top getFirstTop() {
        if (tops.isEmpty()) {
            return null;
        }
        return getTop(0);
    }

    public int size() {
        return allTops.size();
    }

    /**
     * создать список топов который может быть
     */
    public void createTop(Top topType, List<Location> locations) {
        if (tops.contains(topType)) {
            return;
        }

        List<StandTop> standTops = new ArrayList<>();

        int pos = 1;
        for (Location location : locations) {
            standTops.add(new StandTop(topType, location, pos++));
        }

        tops.add(topType);
        allTops.put(topType, standTops);
    }

    /**
     * задать данные для топа
     * @param standTopData - новые данные для стенда
     */
    public void updateStandData(@NotNull List<StandTopData> standTopData) {
        for (StandTopData data : standTopData) {
            if (!tops.contains(data.getTop())) {
                continue;
            }

            StandTop standTop = getStandByPosition(data.getTop(), data.getPosition());
            if (standTop != null) {
                standTop.setStandTopData(data);
            }
        }
    }

    /**
     * получить определенный стенд
     * @param top - какого топа
     * @param position - какой позиции
     * @return - стенд
     */
    @Nullable
    public StandTop getStandByPosition(Top top, int position) {
        List<StandTop> allStands = getAllStands(top);
        if (allStands.size() < position) {
            return null;
        }
        return allStands.get(position - 1);
    }

    public List<StandTop> getAllStands(Top top) {
        List<StandTop> standTops = allTops.get(top);
        if (standTops != null) {
            return Collections.unmodifiableList(standTops);
        }
        return Collections.emptyList();
    }

    public @NotNull List<StandTop> getAllStands() {
        List<StandTop> standTops = new ArrayList<>();
        allTops.values().forEach(standTops::addAll);
        return standTops;
    }
}
