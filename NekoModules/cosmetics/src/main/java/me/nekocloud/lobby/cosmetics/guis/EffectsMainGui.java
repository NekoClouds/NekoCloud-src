package me.nekocloud.lobby.cosmetics.guis;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.gui.AbstractGui;
import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.inventory.type.DInventory;
import me.nekocloud.api.manager.GuiManager;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.util.StringUtil;
import me.nekocloud.lobby.cosmetics.api.CosmeticsAPI;
import me.nekocloud.lobby.cosmetics.api.Effect;
import me.nekocloud.lobby.cosmetics.api.EffectType;
import me.nekocloud.lobby.cosmetics.api.manager.CosmeticManager;
import me.nekocloud.lobby.cosmetics.api.player.CosmeticPlayer;
import me.nekocloud.lobby.cosmetics.guis.effects.ArrowsEffectGui;
import me.nekocloud.lobby.cosmetics.guis.effects.CritsEffectGui;
import me.nekocloud.lobby.cosmetics.guis.effects.KillsEffectGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class EffectsMainGui extends AbstractGui<DInventory> {

    static CosmeticManager COSMETIC_MANAGER = CosmeticsAPI.getCosmeticManager();
    static GuiManager<AbstractGui<?>> GUI_MANAGER = NekoCloud.getGuiManager();

    public static int[] SLOTS = new int[]{ 10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29 };

    CosmeticPlayer cosmeticPlayer;

    public EffectsMainGui(Player player) {
        super(player);

        cosmeticPlayer = COSMETIC_MANAGER.getCosmeticPlayer(player);
        createInventory();
    }

    protected void createInventory() {
        inventory = INVENTORY_API.createInventory(player,
                gamer.getLanguage().getMessage("EFFECTS_GUI_NAME"), 5);
    }

    protected void setItems() {
        val lang = gamer.getLanguage();
//        INVENTORY_API.backButton(this.inventory,
//                (clicker, clickType, slot) -> GUI_MANAGER.getGui(ProfileMainPage.class, player).open(), 40);
        inventory.setItem( 3, 3, new DItem(ItemUtil.getBuilder(Material.TIPPED_ARROW)
                .removeFlags()
                .setName(lang.getMessage("ARROWS_EFFECTS_NAME"))
                .setLore(lang.getList("ARROWS_EFFECTS_LORE",
                        cosmeticPlayer.getParticles().get(EffectType.ARROWS).size(),
                        Effect.values().length,
                        StringUtil.onPercent(cosmeticPlayer.getParticles().get(EffectType.ARROWS).size(),
                                Effect.values().length) + "%"))
                .build(), (player, clickType, i) -> (GUI_MANAGER.getGui(ArrowsEffectGui.class, player)).open()));

        inventory.setItem(5, 3, new DItem(ItemUtil.getBuilder(Material.REDSTONE)
                .setName(lang.getMessage("CRITS_EFFECTS_NAME"))
                .setLore(lang.getList("CRITS_EFFECTS_LORE",
                        cosmeticPlayer.getParticles().get(EffectType.CRITS).size(),
                        Effect.values().length,
                        StringUtil.onPercent(cosmeticPlayer.getParticles().get(EffectType.CRITS).size(),
                                Effect.values().length) + "%"))
                .build(), (player, clickType, i) -> (GUI_MANAGER.getGui(CritsEffectGui.class, player)).open()));

        inventory.setItem(7, 3, new DItem(ItemUtil.getBuilder(Material.DIAMOND_SWORD)
                .setName(lang.getMessage("KILLS_EFFECTS_NAME"))
                .setLore(lang.getList("KILLS_EFFECTS_LORE",
                        cosmeticPlayer.getParticles().get(EffectType.KILLS).size(),
                        Effect.values().length,
                        StringUtil.onPercent(cosmeticPlayer.getParticles().get(EffectType.KILLS).size(),
                                Effect.values().length) + "%"))
                .build(), (player, clickType, i) -> (GUI_MANAGER.getGui(KillsEffectGui.class, player)).open()));
    }
}

