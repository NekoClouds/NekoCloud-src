package me.nekocloud.box.gui;

import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.inventory.InventoryAPI;
import me.nekocloud.api.inventory.type.DInventory;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.sound.SoundAPI;
import me.nekocloud.api.util.Head;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.api.util.Rarity;
import me.nekocloud.base.SoundType;
import me.nekocloud.base.gamer.constans.KeyType;
import me.nekocloud.base.gamer.sections.NetworkingSection;
import me.nekocloud.base.util.StringUtil;
import me.nekocloud.box.api.BoxAPI;
import me.nekocloud.box.api.ItemBox;
import me.nekocloud.box.api.ItemBoxManager;
import me.nekocloud.box.data.Box;
import org.bukkit.Material;

import java.util.*;

public class PlayerBoxGui {

    private static final ItemBoxManager ITEM_BOX_MANAGER = BoxAPI.getItemBoxManager();
    private static final Random RANDOM = new Random();
    private static final SoundAPI SOUND_API = NekoCloud.getSoundAPI();
    private static final InventoryAPI INVENTORY_API = NekoCloud.getInventoryAPI();

    private final DInventory inventory;
    private final BukkitGamer gamer;
    private final Box box;

    public PlayerBoxGui(BukkitGamer gamer, Box box) {
        this.gamer = gamer;
        this.box = box;

        val language = gamer.getLanguage();

        inventory = INVENTORY_API.createInventory(gamer.getPlayer(), language.getMessage("BOX_GUI_NAME"), 6);
        for (int slot : BoxAPI.EMPTY_SLOTS_PURPLE) {
            inventory.setItem(slot, new DItem(ItemUtil.getBuilder(Material.STAINED_GLASS_PANE)
                    .setName(" ")
                    .setDurability((short) 10)
                    .build()));
        }
        for (int slot : BoxAPI.EMPTY_SLOTS_MAGENTA) {
            inventory.setItem(slot, new DItem(ItemUtil.getBuilder(Material.STAINED_GLASS_PANE)
                    .setName(" ")
                    .setDurability((short) 2)
                    .build()));
        }

        inventory.setItem(48, new DItem(ItemUtil.getBuilder(Material.BOOK)
                .setName(language.getMessage("BOX_GUI_HELP_NAME"))
                .setLore(language.getList("BOX_GUI_HELP_LORE"))
                .build()));

        inventory.setItem(50, new DItem(ItemUtil.getBuilder(Material.PAPER)
                .setName(language.getMessage("BOX_GUI_HELP2_NAME"))
                .setLore(language.getList("BOX_GUI_HELP2_LORE"))
                .build()));
    }

    public void open() {
        if (box.isWork()) {
            gamer.getPlayer().closeInventory();
            return;
        }
        box.getPlayersOpenGui().add(gamer.getName()); //чтобы потом если что закрыть ему гуи

        inventory.openInventory(gamer.getPlayer());

        val language = gamer.getLanguage();
        for (val keyType : KeyType.values()) {
            int amount = gamer.getKeys(keyType);
            boolean enable = amount > 0;
            val item = enable ? Head.getHeadByValue(keyType.getHeadValue()) : ItemUtil.getBuilder(Material.STAINED_GLASS)
                    .setDurability((short) 14)
                    .build();
            inventory.setItem(keyType.getSlotGui(), new DItem(ItemUtil.getBuilder(item)
                    .setName(keyType.getName(language))
                    .setLore(keyType.getLore(language))
                    .addLore(language.getList("BOX_GUI_LORE", StringUtil.getNumberFormat(amount)))
                    .setAmount(enable ? (amount > 64 ? 64 : amount) : 1)
                    .build(), (player, clickType, i) -> {
                box.getPlayersOpenGui().remove(gamer.getName());

                if (clickType.isLeftClick()) {
                    PlayerBoxGui.this.onClick(keyType);
                    return;
                }

                val boughtKeyType = BoughtKeyType.getBoughtType(keyType);
                if (boughtKeyType == null) {
                    gamer.sendMessageLocale("BOX_KEY_NOT_BUY");
                    player.closeInventory();
                    return;
                }

                val shopBoxGui = new ShopBoxGui(this, gamer, boughtKeyType);
                shopBoxGui.open();
            }));
        }
    }

    private void onClick(KeyType keyType) {
        if (!isEnableOpen(keyType)) {
            gamer.sendMessageLocale("BOX_NO_OPEN");
            gamer.getPlayer().closeInventory();
            return;
        }

        val section = gamer.getSection(NetworkingSection.class);
        if (section == null) {
            gamer.sendMessage("§cОшибка секции, сообщите админам в vk.com/nekocloud");
            return;
        }

        if (gamer.getKeys(keyType) < 1 || ITEM_BOX_MANAGER.getItems(keyType).isEmpty()) {
            SOUND_API.play(gamer.getPlayer(), SoundType.NOTE_BASS);
            gamer.getPlayer().closeInventory();
            return;
        }

        val lang = gamer.getLanguage();

        int randomNumber = section.getRandom().getOrDefault(keyType, 0);

        List<ItemBox> items = new ArrayList<>();
        for (int i = 1; i <= BoxAPI.getAmountItems(); i++) {
            items.add(getRandomItemByRarity(keyType, getRandomRarity(randomNumber)));
        }

        gamer.sendMessageLocale("BOX_OPEN_YOU");
        val winItem = items.get(BoxAPI.getAmountItems() - 1);
        winItem.onApply(gamer);

//        BukkitBalancePacket packet = new BukkitBalancePacket(gamer.getPlayerID(),
//                BukkitBalancePacket.Type.KEYS,
//                keyType.getId(),
//                -1,
//                true);
//        BukkitConnector.getInstance().sendPacket(packet);

        section.openKeys(keyType, winItem.getRarity().getRarity() > 0 ? 0 : randomNumber + 1);

        //todo
        box.onStart(gamer, items, winItem, Head.getHeadByValue(keyType.getHeadValue()));

        gamer.getPlayer().closeInventory();
    }

    private static Rarity getRandomRarity(int pseudoRandom) {
        List<Rarity> rarities = Arrays.asList(Rarity.values());
        rarities.sort(Comparator.comparingDouble(Rarity::getChance));
        double randChance = Math.random();
        for (Rarity rarity : rarities) {
            if (rarity.getChance() + rarity.getChangeChance() * pseudoRandom > randChance) {
                return rarity;
            }
        }

        return rarities.get(rarities.size() - 1);
    }

    private static ItemBox getRandomItemByRarity(KeyType keyType, Rarity rarity) {
        List<ItemBox> itemBoxes = ITEM_BOX_MANAGER.getItems(keyType, rarity);
        return itemBoxes.get(RANDOM.nextInt(itemBoxes.size()));
    }

    private static boolean isEnableOpen(KeyType keyType) {
        return true;
    }
//        val serverSubModeType =
//                ServerMode.getSubMode(CoreConnector.getInstance().getServerName()).getType();
//        switch (serverSubModeType) {
//            case GAME_LOBBY:
//            case MAIN: {
//                return keyType != KeyType.ITEMS_KEY;
//            }
//            case SURVIVAL: {
//                return keyType != KeyType.GAME_KEY
//                        && keyType != KeyType.COSMETICS_KEY;
//            }
//            case GAME_ARENA: {
//                return false;
//            }
//        }
//        return false;
//    }
}
