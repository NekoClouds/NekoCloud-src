package me.nekocloud.box.gui;

import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.inventory.InventoryAPI;
import me.nekocloud.api.inventory.type.DInventory;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.sound.SoundAPI;
import me.nekocloud.api.util.Head;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.SoundType;
import me.nekocloud.base.gamer.constans.KeyType;
import me.nekocloud.base.gamer.constans.PurchaseType;
import me.nekocloud.base.locale.CommonWords;
import me.nekocloud.base.locale.Language;
import me.nekocloud.nekoapi.guis.CustomItems;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ShopBoxGui {

    private static final SoundAPI SOUND_API = NekoCloud.getSoundAPI();
    private static final InventoryAPI INVENTORY_API = NekoCloud.getInventoryAPI();

    private final BukkitGamer gamer;
    private final DInventory inventory;
    private final BoughtKeyType boughtKeyType;
    private final ItemStack keyTypeItem;

    public ShopBoxGui(PlayerBoxGui boxGui, BukkitGamer gamer, BoughtKeyType boughtKeyType) {
        this.gamer = gamer;
        this.boughtKeyType = boughtKeyType;

        Language language = gamer.getLanguage();
        KeyType keyType = boughtKeyType.getKeyType();

        keyTypeItem = Head.getHeadByValue(keyType.getHeadValue());

        this.inventory = INVENTORY_API.createInventory(gamer.getPlayer(), language.getMessage("BOX_SHOP_GUI_NAME"),6);

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

        inventory.setItem(4, new DItem(ItemUtil.getBuilder(keyTypeItem.clone())
                .setName(keyType.getName(language))
                .setLore(keyType.getLore(language))
                .build()));

        inventory.setItem(49, new DItem(CustomItems.getBack(language),
                (player, clickType, i) -> boxGui.open()));

        initItems(11, 1);
        initItems(13, 3);
        initItems(15, 5);
    }

    public void open() {
        inventory.openInventory(gamer.getPlayer());
    }

    private void initItems(int slot, int amount) {
        Language language = gamer.getLanguage();

        KeyType keyType = boughtKeyType.getKeyType();

        inventory.setItem(slot, new DItem(ItemUtil.getBuilder(keyTypeItem.clone())
                .setName(keyType.getName(language))
                .setLore(language.getMessage("BOX_BUY_LORE_MAIN",
                        amount,
                        CommonWords.KEYS_1.convert(amount, language)))
                .setAmount(amount)
                .build()));

        //coins
        boolean money = boughtKeyType.getPriceMoney() > 0;
        inventory.setItem(slot + 9, new DItem(ItemUtil.getBuilder(money ? new ItemStack(Material.IRON_INGOT, 1) :
                ItemUtil.getBuilder(Material.STAINED_GLASS_PANE)
                        .setDurability((short) 14)
                        .build())
                .setAmount(money ? boughtKeyType.getPriceMoney() * amount : 1)
                .setName(language.getMessage("BOX_BUY_MONEY"))
                .setLore(money ? language.getList("BOX_BUY_LORE",
                        amount,
                        CommonWords.KEYS_1.convert(amount, language),
                        "§6" + boughtKeyType.getPriceMoney() * amount,
                        CommonWords.COINS_1.convert(boughtKeyType.getPriceMoney() * amount,language)
                ) : language.getList("BOX_NO_BUY_LORE")).build(), (player, clickType, i) -> {
                    if (!money) {
                        SOUND_API.play(player, SoundType.NO);
                        return;
                    }

                    if (gamer.changeMoney(PurchaseType.COINS, -boughtKeyType.getPriceMoney() * amount)) {
                        gamer.changeKeys(keyType, amount);
                        SOUND_API.play(player, SoundType.SELECTED);
                    }
                }));

        //virts
        boolean virt = boughtKeyType.getPriceVirt() > 0;
        inventory.setItem(slot + 18, new DItem(ItemUtil.getBuilder(virt ? new ItemStack(Material.GOLD_INGOT, 1) :
                ItemUtil.getBuilder(Material.STAINED_GLASS_PANE)
                        .setDurability((short) 14)
                        .build())
                .setName(language.getMessage("BOX_BUY_GOLD"))
                .setAmount(virt ? boughtKeyType.getPriceVirt() * amount : 1)
                .setLore(virt ? language.getList("BOX_BUY_LORE",
                        amount,
                        CommonWords.KEYS_1.convert(amount, language),
                        "§e" + boughtKeyType.getPriceVirt() * amount,
                        CommonWords.VIRTS_1.convert(boughtKeyType.getPriceVirt() * amount, language)
                ) : language.getList("BOX_NO_BUY_LORE")).build(), (player, clickType, i) -> {
                    if (!virt) {
                        SOUND_API.play(player, SoundType.NO);
                        return;
                    }

                    if (gamer.changeMoney(PurchaseType.VIRTS, -boughtKeyType.getPriceVirt() * amount)) {
                        gamer.changeKeys(keyType, amount);
                        SOUND_API.play(player, SoundType.SELECTED);
                    }
                }));

        //todo сделать получение ключей через конвертацию

    }
}
