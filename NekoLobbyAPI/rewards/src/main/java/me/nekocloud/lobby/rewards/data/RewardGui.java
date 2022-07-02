package me.nekocloud.lobby.rewards.data;

import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.inventory.InventoryAPI;
import me.nekocloud.api.inventory.type.DInventory;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.TimeUtil;
import me.nekocloud.lobby.rewards.Rewards;
import me.nekocloud.lobby.rewards.bonuses.RewardType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class RewardGui {

    private static final GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();

    private static final InventoryAPI INVENTORY_API = NekoCloud.getInventoryAPI();

    private final Player player;
    private final Rewards rewards;

    private DInventory inventory;

    public RewardGui(Player player, Rewards rewards) {
        this.player = player;
        this.rewards = rewards;

        createInventory();
        update();
    }

    public void createInventory() {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player.getName());
        if (gamer == null)
            return;
        this.inventory = INVENTORY_API.createInventory(gamer.getLanguage().getMessage("REWARD_GUI_NAME"), 6);
    }

    public void update() {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player.getName());
        if (gamer == null)
            return;

        Language language = gamer.getLanguage();
        RewardPlayer rewardPlayer = RewardPlayer.getPlayer(player.getName());

        if (rewardPlayer == null)
            return;

        int slot = 10;

        inventory.clearInventory();

        for (RewardType type : RewardType.values()) {
            long rewardTime = rewardPlayer.getActivationTime(type) + type.getTimeDelay();
            boolean available = rewardTime < System.currentTimeMillis();

            String claimTime = TimeUtil.leftTime(language, rewardTime, true);

            List<String> lore = new ArrayList<>(language.getList(type.getLocaleLoreKey()));
            List<String> actionLore = language.getList("REWARD_" +
                    (available ? "CLICK_TO_CLAIM" : "YOU_CAN_CLAIM_IN"), claimTime);
            lore.addAll(actionLore);

            ItemStack rewardItem = ItemUtil.getBuilder(available ? Material.STORAGE_MINECART : Material.HOPPER_MINECART)
                    .setName("§e" + language.getMessage(type.getLocaleKey()))
                    .setLore(lore)
                    .setAmount((int) TimeUnit.MILLISECONDS.toDays(type.getTimeDelay()))
                    .addFlag(ItemFlag.HIDE_ENCHANTS)
                    .build();

            inventory.setItem(slot, new DItem(rewardItem, (target, clickType, clickedSlot) -> {
                if (available) {
                    player.closeInventory();
                    rewardPlayer.activateReward(type, target, rewards);
                }
            }));

            slot += 1;
        }

        ItemStack donateItem = ItemUtil.getBuilder(Material.EXP_BOTTLE)
                .setName(language.getMessage("REWARD_DONATE_ITEM"))
                .setLore(language.getList("REWARD_DONATE_ITEM_LORE"))
                .build();

        inventory.setItem(40, new DItem(donateItem));
    }

    public void open() {
        if (inventory == null)
            return;
        inventory.openInventory(player);
    }
}
