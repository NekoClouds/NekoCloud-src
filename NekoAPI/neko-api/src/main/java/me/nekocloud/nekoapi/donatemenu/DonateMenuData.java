package me.nekocloud.nekoapi.donatemenu;

import lombok.Getter;
import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.base.locale.Language;
import me.nekocloud.nekoapi.donatemenu.guis.*;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@Getter
public final class DonateMenuData {

    private static final GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();

    private final Map<String, DonateMenuGui> data = new HashMap<>();

    DonateMenuData(Player player) {
        Language language = Language.DEFAULT;
        val gamer = GAMER_MANAGER.getGamer(player);
        if (gamer != null) {
            language = gamer.getLanguage();
        }

        register(MainDonateMenuGui.class, player, language);
        register(PrefixGui.class, player, language);
        register(FastMessageGui.class, player, language);
        register(JoinMessageGui.class, player, language);
    }

    private void register(Class<? extends DonateMenuGui> clazz, Player player, Language language) {
        try {
            val gui = clazz.getConstructor(Player.class, DonateMenuData.class, Language.class)
                    .newInstance(player, this, language);
            this.data.put(clazz.getSimpleName().toLowerCase(), gui);
        } catch (Exception ignored) {}
    }

    public void updateClass(DonateMenuGui donateMenuGui, Player player, Language language) {
        this.data.remove(donateMenuGui.getClass().getSimpleName().toLowerCase());
        register(donateMenuGui.getClass(), player, language);
    }

    public <T extends DonateMenuGui> T get(Class<T> clazz) {
        return (T) data.get(clazz.getSimpleName().toLowerCase());
    }
}
