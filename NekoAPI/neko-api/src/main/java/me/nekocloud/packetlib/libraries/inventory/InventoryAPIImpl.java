package me.nekocloud.packetlib.libraries.inventory;

import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.sound.SoundAPI;
import me.nekocloud.api.util.Head;
import me.nekocloud.base.SoundType;
import me.nekocloud.packetlib.libraries.inventory.inventories.CraftDInventory;
import me.nekocloud.packetlib.libraries.inventory.inventories.CraftMultiInventory;
import me.nekocloud.packetlib.libraries.inventory.inventories.CraftScrollInventory;
import me.nekocloud.api.inventory.DItem;
import me.nekocloud.api.inventory.InventoryAPI;
import me.nekocloud.api.inventory.action.InventoryAction;
import me.nekocloud.api.inventory.type.DInventory;
import me.nekocloud.api.inventory.type.MultiInventory;
import me.nekocloud.api.inventory.type.ScrollInventory;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.locale.Language;
import me.nekocloud.nekoapi.loader.NekoAPI;
import org.bukkit.entity.Player;

import java.util.List;

public class InventoryAPIImpl implements InventoryAPI {

    private final GuiManagerListener listener;
    protected static final SoundAPI SOUND_API = NekoCloud.getSoundAPI();

    public InventoryAPIImpl(NekoAPI nekoAPI) {
        this.listener = new GuiManagerListener(nekoAPI);
    }

    @Override
    public DInventory createInventory(Player player, String name, int rows, InventoryAction inventoryAction) {
        return new CraftDInventory(player, name, rows, inventoryAction, listener);
    }

    @Override
    public DInventory createInventory(Player player, String name, int rows) {
        return createInventory(player, name, rows, null);
    }

    @Override
    public DInventory createInventory(String name, int rows) {
        return createInventory(null, name, rows);
    }

    @Override
    public DInventory createInventory(Player player, int rows, Language lang, String key, Object... objects) {
        return createInventory(player, lang.getMessage(key, objects), rows);
    }

    @Override
    public DInventory createInventory(int rows, Language lang, String key, Object... objects) {
        return createInventory(lang.getMessage(key, objects), rows);
    }

    @Override
    public MultiInventory createMultiInventory(Player player, String name, int rows) {
        return new CraftMultiInventory(player, name, rows);
    }

    @Override
    public MultiInventory createMultiInventory(String name, int rows) {
        return createMultiInventory(null, name, rows);
    }

    @Override
    public MultiInventory createMultiInventory(Player player, int rows, Language lang, String key, Object... objects) {
        return createMultiInventory(player, lang.getMessage(key, objects), rows);
    }

    @Override
    public MultiInventory createMultiInventory(int rows, Language lang, String key, Object... objects) {
        return createMultiInventory(lang.getMessage(key, objects), rows);
    }

    @Override
    public ScrollInventory createScrollInventory(Player player, String name, Language lang) {
        return new CraftScrollInventory(player, name, lang.getId());
    }

    @Override
    public ScrollInventory createScrollInventory(String name, Language lang) {
        return createScrollInventory(null, name, lang);
    }

    @Override
    public void pageButton(Language lang, int pagesCount, List<DInventory> pages, int slotDown, int slotUp) {
        for (int i = 0; i < pages.size(); i++) {
            int finalI = i;
            if (i == 0 && pagesCount > 1 && pages.size() > 1) {
                (pages.get(i)).setItem(slotUp, new DItem(ItemUtil.getBuilder(Head.RIGHT)
                                .setName(lang.getMessage( "PAGE_ARROW1"))
                                .setLore(lang.getList( "PAGE_ARROW_LORE", (i + 2)))
                                .build(),
                        (player, clickType, slot) -> {
                            pages.get(finalI + 1).openInventory(player);
                            SOUND_API.play(player, SoundType.CLICK); //звук перелистывания страницы
                        }
                ));
            } else if (i > 0 && i < pagesCount - 1 && pages.size() > i + 1) {
                (pages.get(i)).setItem(slotDown, new DItem(ItemUtil.getBuilder(Head.LEFT)
                                .setName(lang.getMessage( "PAGE_ARROW2"))
                                .setLore(lang.getList("PAGE_ARROW_LORE", i))
                                .build(),
                        (player, clickType, slot) -> {
                            pages.get(finalI - 1).openInventory(player);
                            SOUND_API.play(player, SoundType.CLICK); //звук перелистывания страницы
                        }
                ));
                (pages.get(i)).setItem(slotUp, new DItem(ItemUtil.getBuilder(Head.RIGHT)
                                .setName(lang.getMessage("PAGE_ARROW1"))
                                .setLore(lang.getList("PAGE_ARROW_LORE", (i + 2)))
                                .build(),
                        (player, clickType, slot) -> {
                            pages.get(finalI + 1).openInventory(player);
                            SOUND_API.play(player, SoundType.CLICK); //звук перелистывания страницы
                        }
                ));
            } else if (pages.size() > 1 && pagesCount > 1) {
                (pages.get(i)).setItem(slotDown, new DItem(ItemUtil.getBuilder(Head.LEFT)
                                .setName(lang.getMessage( "PAGE_ARROW2"))
                                .setLore(lang.getList("PAGE_ARROW_LORE", i))
                                .build(),
                        (player, clickType, slot) -> {
                            pages.get(finalI - 1).openInventory(player);
                            SOUND_API.play(player, SoundType.CLICK); //звук перелистывания страницы
                        }
                ));
            }
        }
    }

    @Override
    public void pageButton(Language lang, int pagesCount, MultiInventory inventory, int slotDown, int slotUp) {
        pageButton(lang, pagesCount, inventory.getInventories(), slotDown, slotUp);
    }
}
