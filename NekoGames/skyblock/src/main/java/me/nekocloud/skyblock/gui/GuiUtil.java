package me.nekocloud.skyblock.gui;

import lombok.experimental.UtilityClass;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.inventory.InventoryAPI;
import me.nekocloud.api.inventory.type.DInventory;
import me.nekocloud.api.inventory.type.MultiInventory;
import me.nekocloud.api.manager.GuiManager;
import me.nekocloud.api.sound.SoundAPI;
import me.nekocloud.base.SoundType;
import me.nekocloud.base.locale.Language;
import me.nekocloud.nekoapi.guis.CustomItems;
import me.nekocloud.skyblock.gui.guis.ProfileGui;
import me.nekocloud.skyblock.api.SkyBlockAPI;
import me.nekocloud.skyblock.api.SkyBlockGui;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class GuiUtil {

    private static final InventoryAPI API = NekoCloud.getInventoryAPI();
    private static final SoundAPI SOUND_API = NekoCloud.getSoundAPI();
    private static final GuiManager<SkyBlockGui> SKY_GUI_MANAGER = SkyBlockAPI.getSkyGuiManager();

    public void setBack(DInventory inventory, Language lang) {
        ItemStack backItem = CustomItems.getBack(lang);

        inventory.setItem(4 * 9 + 4, new DItem(backItem, (player, clickType, i) -> {
            ProfileGui profileGui = SKY_GUI_MANAGER.getGui(ProfileGui.class, player);
            if (profileGui != null)
                profileGui.open();
            SOUND_API.play(player, SoundType.PICKUP);
        }));
    }

    public void setBack(MultiInventory multiInventory, Language lang) {
        multiInventory.getInventories().forEach(inventory -> setBack(inventory, lang));
    }
}
