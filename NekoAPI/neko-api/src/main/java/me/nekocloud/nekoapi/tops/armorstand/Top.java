package me.nekocloud.nekoapi.tops.armorstand;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.hologram.Hologram;
import me.nekocloud.api.hologram.HologramAPI;
import me.nekocloud.base.locale.Language;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class Top {

    protected static final HologramAPI HOLOGRAM_API = NekoCloud.getHologramAPI();

    protected final TopManager standTopManager;
    protected final Location location;
    protected final Int2ObjectMap<Hologram> holograms = new Int2ObjectOpenHashMap<>();

    /**
     * получить центральную голограмму
     * @param language - язык для которого ее получить
     * @return - голограмма
     */
    public final Hologram getHologramMiddle(@NotNull Language language) {
        return holograms.get(language.getId());
    }

    public final List<StandTop> getStands() {
        return standTopManager.getAllStands(this);
    }

    /**
     * где находится топ
     * @return - где он
     */
    public final Location getLocation() {
        return location.clone();
    }

    /**
     * получить порядковый номер этого типа
     * @return - порядковый номер (нужен для БД)
     */
    public final int getId() {
        int id = 0;

        for (Top topType : standTopManager.getTops()) {
            if (topType == this) {
                break;
            }

            id++;
        }

        return id;
    }
}
