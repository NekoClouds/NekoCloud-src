package me.nekocloud.skyblock.gui;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.inventory.InventoryAPI;
import me.nekocloud.api.inventory.type.DInventory;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.api.sound.SoundAPI;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.SoundType;
import me.nekocloud.base.locale.Language;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class AcceptGui {

    private static final InventoryAPI API = NekoCloud.getInventoryAPI();
    private static final SoundAPI SOUND_API = NekoCloud.getSoundAPI();
    private static final GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();

    private Player player;
    private DInventory inventory;
    private Language lang;
    private Type type;

    public AcceptGui(Player player, Type type) {
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null)
            return;

        this.player = player;
        this.type = type;
        this.lang = gamer.getLanguage();
        String name = lang.getMessage(type.getNameGuiKey());
        inventory = API.createInventory(player, name, 5);
    }

    public void open(Runnable yes, Runnable no) {
        if (inventory == null || yes == null)
            return;

        inventory.setItem(2 * 9 + 2, new DItem(ItemUtil.getBuilder(Material.STAINED_GLASS)
                .setName("§a" + lang.getMessage("CONFIRMED_NAME"))
                .setLore(lang.getList(type.getLoreKey()))
                .setDurability((short) 5)
                .build(), (player, clickType, slot) -> {
                    yes.run();
                    SOUND_API.play(player, SoundType.CLICK);
        }));

        inventory.setItem(2 * 9 + 6, new DItem(ItemUtil.getBuilder(Material.STAINED_GLASS)
                .setName(lang.getMessage("CANCEL_NAME"))
                .setLore(lang.getList("ACCEPT_LORE_NO"))
                .setDurability((short) 14)
                .build(), (player, clickType, i) -> {
                    SOUND_API.play(player, SoundType.NO);
                    if (no != null)
                          no.run();
                    else
                        player.closeInventory();
        }));

        inventory.openInventory(player);
    }

    @AllArgsConstructor
    @Getter
    public enum Type {
        RESET("ISLAND_RESET_GUI_NAME", "ISLAND_RESET_LORE"),
        DELETE("ISLAND_DELETE_GUI_NAME", "ISLAND_DELETE_LORE"),
        LEAVE("ISLAND_LEAVE_GUI_NAME", "ISLAND_LEAVE_LORE"),
        BUY_BIOME("ISLAND_BUY_BIOME_GUI_NAME", "ISLAND_BUY_BIOME"),
        BUY_UPGRADE("ISLAND_BUY_UPGRADE_GUI_NAME", "ISLAND_BUY_UPGRADE"),
        REMOVE_MEMBER("ISLAND_REMOVE_MEMBER_GUI_NAME", "ISLAND_REMOVE_MEMBER_LORE"),
        ;

        private final String nameGuiKey;
        private final String loreKey;
    }
}
