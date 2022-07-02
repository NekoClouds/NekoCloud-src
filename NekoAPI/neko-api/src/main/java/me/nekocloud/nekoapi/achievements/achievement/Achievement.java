package me.nekocloud.nekoapi.achievements.achievement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.locale.Language;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public abstract class Achievement {

    private static final GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();

    @Getter
    private final int id;
    private final ItemStack itemStack;
    private final String nameKey;
    private final String loreKey;

    public ItemStack getItem() {
        return itemStack.clone();
    }

    public ItemStack getItemStack(Language lang, Object... objects) {
        return ItemUtil.getBuilder(itemStack.clone())
                .setName(lang.getMessage(nameKey))
                .setLore(lang.getList(loreKey, objects))
                .build();
    }

    public ItemStack getItemStack(Player player, Object... objects) {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null)
            return getItemStack(Language.DEFAULT);

        return getItemStack(gamer.getLanguage(), objects);
    }

    public String getName(Player player) {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null)
            return getName(Language.DEFAULT);

        return getName(gamer.getLanguage());
    }

    public String getName(Language lang) {
        return lang.getMessage(nameKey);
    }

    public final String getLoreKey() {
        return loreKey;
    }

    /**
     * узнать сколько % ачивки выполнено
     * @return - сколько %
     */
    public abstract int getPercent(AchievementPlayer achievementPlayer);

    /**
     * кол-во очков за выполнение ачивки
     * @return - кол-во очков
     */
    public abstract int getPoints();

    /**
     * вызывается когда ачивка была выполнен
     * @param player - кто выполнил
     */
    protected abstract void complete(BukkitGamer gamer);
}