package me.nekocloud.lobby.game.top;

import lombok.Getter;
import lombok.val;
import me.nekocloud.api.hologram.Hologram;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.TimeUtil;
import me.nekocloud.nekoapi.tops.HologramAnimation;
import me.nekocloud.nekoapi.tops.armorstand.Top;
import me.nekocloud.nekoapi.tops.armorstand.TopManager;
import org.bukkit.Location;

@Getter
public class StatsTop extends Top {

    private final TopTable topTable;
    private final int timeMinutes;

    public StatsTop(int timeMinutes, TopTable topTable, TopManager standTopManager, Location location) {
        super(standTopManager, location);
        this.topTable = topTable;
        this.timeMinutes = timeMinutes;
    }

    public Hologram apply(Language language) {
        val hologram = HOLOGRAM_API.createHologram(location.clone());
        val holoName = topTable.getHoloName();

        if (topTable.getType() == StatsType.ALL) {
            hologram.addTextLine(language.getList(topTable.getLocaleKey() + "_ALL", holoName));
        } else {
            val date = TimeUtil.getMonthName(TimeUtil.getTotalMonth(), language);
            hologram.addTextLine(language.getList(topTable.getLocaleKey() + "_MONTHLY", holoName, date));
        }

        hologram.addAnimationLine(10L, new HologramAnimation(language, timeMinutes));
        hologram.addTextLine(language.getMessage("HOLO_TOP_MAIN"));

        return hologram;
    }
}
