package me.nekocloud.nekoapi.achievements.gui;

import me.nekocloud.nekoapi.achievements.manager.AchievementManager;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.inventory.action.ClickAction;
import me.nekocloud.api.inventory.InventoryAPI;
import me.nekocloud.api.inventory.type.MultiInventory;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.api.util.InventoryUtil;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.locale.Language;
import me.nekocloud.nekoapi.achievements.achievement.Achievement;
import me.nekocloud.nekoapi.achievements.achievement.AchievementPlayer;
import me.nekocloud.nekoapi.guis.CustomItems;
import me.nekocloud.api.inventory.DItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Deprecated //todo удалить
public class AchievementGui {

    protected static final InventoryAPI API = NekoCloud.getInventoryAPI();
    protected static final GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();

    private final Player player;
    private MultiInventory inventory;
    private Language lang;

    public AchievementGui(Player player, String key) {
        this.player = player;

        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null)
            return;

        this.lang = gamer.getLanguage();
        this.inventory = API.createMultiInventory(player, lang.getMessage(key), 5);
    }

    public void setItems(AchievementManager achievementManager) {
        if (player == null || inventory == null || achievementManager == null)
            return;

        AchievementPlayer achievementPlayer = achievementManager.getPlayerManager().getAchievementPlayer(player);
        if (achievementPlayer == null)
            return;

        int slot = 10;
        int page = 0;
        for (Achievement achievement : achievementManager.getAchievements().values()) {
            ItemStack itemStack = ItemUtil.getBuilder(achievement.getItemStack(lang))
                    .setName("§a" + achievement.getName(lang))
                    .addLore("")
                    .addLore(lang.getMessage("ACHIEVEMENT_DONE"))
                    .build();

            if (!achievementPlayer.hasAchievement(achievement))
                itemStack = ItemUtil.getBuilder(Material.STAINED_GLASS_PANE)
                        .setDurability((short) 14)
                        .setName("§c" + achievement.getName(lang))
                        .setLore(lang.getList( achievement.getLoreKey()))
                        .addLore("")
                        .addLore(lang.getMessage("ACHIEVEMENT_PERCENT",
                                achievement.getPercent(achievementPlayer) + "%"))
                        .addLore("")
                        .addLore(lang.getMessage( "ACHIEVEMENT_NO_DONE"))
                        .build();

            inventory.setItem(page, slot, new DItem(itemStack));

            slot++;

            if ((slot - 8) % 9 == 0)
                slot += 2;

            if (slot >= 35) {
                slot = 10;
                page++;
            }
        }

        int pagesCount = InventoryUtil.getPagesCount(achievementManager.getAchievements().size(), 21);
        API.pageButton(lang, pagesCount, inventory, 38, 42);
    }

    public void addBackItem(ClickAction clickAction) {
        if (inventory == null)
            return;

        inventory.setItem(40, new DItem(CustomItems.getBack(lang), clickAction));
    }

    public void open() {
        if (player == null || inventory == null)
            return;

        inventory.openInventory(player);
    }
}
