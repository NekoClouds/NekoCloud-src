package me.nekocloud.skyblock.gui.guis;

import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.SoundType;
import me.nekocloud.base.locale.CommonWords;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.StringUtil;
import me.nekocloud.skyblock.api.island.Island;
import me.nekocloud.skyblock.api.island.member.MemberType;
import me.nekocloud.skyblock.gui.AcceptGui;
import me.nekocloud.skyblock.gui.GuiUtil;
import me.nekocloud.skyblock.module.BorderModule;
import me.nekocloud.skyblock.api.SkyBlockGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class UpgradeGui extends SkyBlockGui {

    public UpgradeGui(Player player) {
        super(player, "SKYBLOCK_UPGRADE_GUI_NAME");
    }

    @Override
    protected void setItems(Player player) {
        Island island = ISLAND_MANAGER.getIsland(player);
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null || island == null)
            return;

        GuiUtil.setBack(inventory, lang);

        MemberType memberType = island.getMemberType(gamer.getPlayerID());

        BorderModule module = island.getModule(BorderModule.class);
        if (module == null)
            return;

        boolean can = (module.getPercent() != 100);
        inventory.setItem(10, new DItem(ItemUtil.getBuilder(Material.SPRUCE_DOOR_ITEM)
                .setName((can ? "§a" : "§c") + lang.getMessage("ISLAND_UPGRADE_TERRITORY_NAME"))
                .setLore(lang.getList("ISLAND_UPGRADE_TERRITORY_LORE",
                        String.valueOf(can ? (module.getLevel() - 1) : 5),
                        StringUtil.getNumberFormat(module.getPriceToUpgrade())
                )).build(), (clicker, clickType, slot) -> {
            if (memberType == MemberType.MEMBER || memberType == MemberType.NOBODY) {
                gamer.sendMessageLocale("ISLAND_NOT_OWNER");
                clicker.closeInventory();
                return;
            }

            if (!can) {
                gamer.sendMessageLocale("ISLAND_MAX_UPGRADE");
                clicker.closeInventory();
                return;
            }

            AcceptGui acceptGui = new AcceptGui(player, AcceptGui.Type.BUY_UPGRADE);
            acceptGui.open(() -> {
                if (!island.changeMoney(-module.getPriceToUpgrade())) {
                    gamer.sendMessageLocale("ISLAND_UPGRADE_BUY_ERROR");
                    SOUND_API.play(player, SoundType.NO);
                    player.closeInventory();
                    return;
                }
                island.getOnlineMembers().forEach(member -> {
                    BukkitGamer memberGamer = GAMER_MANAGER.getGamer(member);
                    if (memberGamer != null) {
                        Language lang = memberGamer.getLanguage();
                        memberGamer.sendMessageLocale("ISLAND_BUY_UPGRADE_TERRITORY",
                                gamer.getChatName(),
                                StringUtil.getNumberFormat(module.getPriceToUpgrade()),
                                CommonWords.COINS_1.convert(module.getPriceToUpgrade(), lang)
                        );
                    }
                });
                module.upgrade();
                clicker.closeInventory();
            }, () -> {
                UpgradeGui upgradeGui = SKY_GUI_MANAGER.getGui(UpgradeGui.class, player);
                if (upgradeGui != null)
                    upgradeGui.open();
            });

        }));
    }
}
