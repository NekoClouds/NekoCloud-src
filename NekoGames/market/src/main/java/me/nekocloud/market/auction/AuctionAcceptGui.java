package me.nekocloud.market.auction;

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
import me.nekocloud.base.util.StringUtil;
import me.nekocloud.market.api.AuctionItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class AuctionAcceptGui {

    private static final InventoryAPI API = NekoCloud.getInventoryAPI();
    private static final SoundAPI SOUND_API = NekoCloud.getSoundAPI();
    private static final GamerManager GAMER_MANAGER = NekoCloud.getGamerManager();

    private final Player player;
    private final AuctionItem auctionItem;
    private DInventory inventory;
    private Language lang;

    public AuctionAcceptGui(Player player, AuctionItem auctionItem) {
        this.player = player;
        this.auctionItem = auctionItem;
        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null)
            return;

        this.lang = gamer.getLanguage();
        String name = lang.getMessage("AUCTION_CONFIRMED_GUI");
        inventory = API.createInventory(player, name, 5);
    }

    public void open(Runnable yes, Runnable no) {
        if (inventory == null || yes == null)
            return;

        inventory.setItem(2 * 9 + 2, new DItem(ItemUtil.getBuilder(Material.STAINED_GLASS)
                .setName("§a" + lang.getMessage("CONFIRMED_NAME"))
                .setLore(lang.getList("AUCTION_CONFIRMED_GUI_LORE",
                        auctionItem.getItem().getType().toString(),
                        auctionItem.getItem().getAmount(),
                        StringUtil.getNumberFormat(auctionItem.getPrice()),
                        auctionItem.getOwner().getDisplayName()))
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
}
