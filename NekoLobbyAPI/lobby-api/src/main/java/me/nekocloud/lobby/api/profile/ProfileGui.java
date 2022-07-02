package me.nekocloud.lobby.api.profile;

import lombok.Getter;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.inventory.InventoryAPI;
import me.nekocloud.api.inventory.type.MultiInventory;
import me.nekocloud.api.manager.GuiManager;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.api.sound.SoundAPI;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.SoundType;
import me.nekocloud.base.locale.Language;
import me.nekocloud.lobby.api.LobbyAPI;
import me.nekocloud.lobby.profile.gui.guis.ProfileMainPage;
import me.nekocloud.nekoapi.guis.CustomItems;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@Getter
public abstract class ProfileGui {

    protected static final InventoryAPI INVENTORY_API = NekoCloud.getInventoryAPI();
    protected static final GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();
    protected static final GuiManager<ProfileGui> GUI_MANAGER = LobbyAPI.getProfileGuiManager();
    protected static final SoundAPI SOUND_API = NekoCloud.getSoundAPI();

    protected final BukkitGamer gamer;

    protected Language lang;
    protected MultiInventory inventory;

    protected ProfileGui(Player player) {
        gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null) {
            return;
        }

        lang = gamer.getLanguage();

        inventory = INVENTORY_API.createMultiInventory(player, lang.getMessage("PROFILE_MAIN_GUI_NAME"), 6);
    }

    protected ProfileGui(Player player, String key) {
        gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null) {
            return;
        }

        lang = gamer.getLanguage();

        inventory = INVENTORY_API.createMultiInventory(player, lang.getMessage(key), 6);
    }

    protected final void setBackItem() {
        inventory.setItem(49, new DItem(CustomItems.getBack(lang), //кнопка назад
                (clicker, clickType, slot) -> {
                    SOUND_API.play(clicker, SoundType.PICKUP);
                    ProfileMainPage mainPage = GUI_MANAGER.getGui(ProfileMainPage.class, clicker);
                    if (mainPage == null) {
                        return;
                    }

                    mainPage.open();
                }));
    }

    /**
     * Установить боковые стеклышки
     */
    protected final void setGlassItems() {
        for (int slot : CustomItems.EMPTY_SLOTS_PURPLE) {
            inventory.setItem(slot, new DItem(ItemUtil.getBuilder(Material.STAINED_GLASS_PANE)
                    .setName(" ")
                    .setDurability((short) 10)
                    .build()));
        }
        for (int slot : CustomItems.EMPTY_SLOTS_MAGENTA) {
            inventory.setItem(slot, new DItem(ItemUtil.getBuilder(Material.STAINED_GLASS_PANE)
                    .setName(" ")
                    .setDurability((short) 2)
                    .build()));
        }
    }

    public final void update() {
        if (gamer == null || inventory == null) {
            return;
        }

        setItems();
    }

    protected abstract void setItems();

    public void open() {
        if (gamer == null || inventory == null) {
            return;
        }

        inventory.openInventory(gamer);
    }


}
