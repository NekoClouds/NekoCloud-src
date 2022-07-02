package me.nekocloud.nekoapi.tops.armorstand;

import lombok.Getter;
import me.nekocloud.api.hologram.Hologram;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.base.locale.Language;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
final class StandPlayer {

    private final TopManager manager;
    private final BukkitGamer gamer;

    private Top topType;
    private boolean newPlayer;

    StandPlayer(@NotNull TopManager manager, BukkitGamer gamer) {
        this.manager = manager;
        this.gamer = gamer;

        Top selectedType = manager.getStandTopSql().getSelectedType(gamer);
        if (selectedType != null) {
            this.topType = selectedType;
            this.newPlayer = false;
        } else {
            this.topType = manager.getFirstTop();
            this.newPlayer = true;
        }

        show();
    }

    void changeSelected() {
        Language language = gamer.getLanguage();

        //скрываем старые
        List<StandTop> stand = manager.getAllStands(topType);
        if (stand != null) {
            for (StandTop standTop : stand) {
                standTop.removeTo(gamer, language, true);
            }
        }
        Hologram hologramMiddle = topType.getHologramMiddle(language);
        if (hologramMiddle != null) {
            hologramMiddle.removeTo(gamer);
        }

        //ищем новый топ
        Top newTopType = manager.getFirstTop();

        int newId = topType.getId() + 1;
        if (manager.getTops().size() > newId) {
            newTopType = manager.getTop(newId);
        }

        this.topType = newTopType;

        //показываем новый топ
        show();

        //пишем в бд новый топ
        manager.getStandTopSql().changeSelectedType(this);

        this.newPlayer = false;
    }

    private void show() {
        Language language = gamer.getLanguage();

        List<StandTop> stand = manager.getAllStands(topType);
        if (stand != null) {
            for (StandTop standTop : stand) {
                standTop.showTo(gamer, language, true);
            }
        }

        Hologram hologramMiddle = topType.getHologramMiddle(language);
        if (hologramMiddle != null) {
            hologramMiddle.showTo(gamer);
        }
    }

    void changeHolo(Language old, Language lang) {
        List<StandTop> stand = manager.getAllStands(topType);
        if (stand != null) {
            for (StandTop standTop : stand) {
                standTop.removeTo(gamer, old, false);
                standTop.showTo(gamer, lang, false);
            }
        }

        Hologram oldHolo = topType.getHologramMiddle(old);
        if (oldHolo != null) {
            oldHolo.removeTo(gamer);
        }

        Hologram newHolo = topType.getHologramMiddle(lang);
        if (newHolo != null) {
            newHolo.showTo(gamer);
        }
    }
}
