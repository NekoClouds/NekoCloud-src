package me.nekocloud.nekoapi.guis;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.experimental.UtilityClass;
import lombok.val;
import me.nekocloud.api.util.Head;
import me.nekocloud.api.util.ItemUtil;
import me.nekocloud.base.locale.Language;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class CustomItems {

    private static final Int2ObjectMap<ItemStack> BACK_ITEMS = new Int2ObjectOpenHashMap<>();
    private static final Int2ObjectMap<ItemStack> BACK_ITEMS2 = new Int2ObjectOpenHashMap<>();
    private static final Int2ObjectMap<ItemStack> ENABLE_ITEM = new Int2ObjectOpenHashMap<>();
    private static final Int2ObjectMap<ItemStack> DISABLE_ITEM = new Int2ObjectOpenHashMap<>();

    public static final IntList EMPTY_SLOTS_PURPLE = new IntArrayList(new int[] {
            0, 1, 7, 8, 9, 17, 36, 44, 45, 46, 52, 53
    }); // ID = 10
    public static final IntList EMPTY_SLOTS_MAGENTA = new IntArrayList(new int[] {
            2, 6, 10, 16, 18, 26, 27, 35, 37, 43, 47, 51
    }); // ID = 2

    public ItemStack getBack(final @NotNull Language lang) {
        val itemStack = BACK_ITEMS.get(lang.getId());
        if (itemStack != null)
            return itemStack.clone();

        return BACK_ITEMS.get(Language.DEFAULT.getId()).clone();
    }

    public ItemStack getBack2(final @NotNull Language lang) {
        ItemStack itemStack = BACK_ITEMS2.get(lang.getId());
        if (itemStack != null)
            return itemStack.clone();

        return BACK_ITEMS2.get(Language.DEFAULT.getId()).clone();
    }

    public ItemStack getEnable(Language lang) {
        val itemStack = ENABLE_ITEM.get(lang.getId());
        if (itemStack != null)
            return itemStack.clone();

        return ENABLE_ITEM.get(Language.DEFAULT.getId()).clone();
    }

    public ItemStack getDisable(Language lang) {
        val itemStack = DISABLE_ITEM.get(lang.getId());
        if (itemStack != null)
            return itemStack.clone();

        return DISABLE_ITEM.get(Language.DEFAULT.getId()).clone();
    }

    private void init() {
        for (val language : Language.values()) {
            val lang = language.getId();
            BACK_ITEMS.put(lang, ItemUtil.getBuilder(Head.BACK)
                    .setName(language.getMessage("PROFILE_BACK_ITEM_NAME"))
                    .setLore(language.getList("PROFILE_BACK_ITEM_LORE"))
                    .build());
            BACK_ITEMS2.put(lang, ItemUtil.getBuilder(Head.BACK)
                    .setName(language.getMessage("PROFILE_BACK_ITEM_NAME"))
                    .setLore(language.getList("PROFILE_BACK_ITEM_LORE2"))
                    .build());
            ENABLE_ITEM.put(lang, ItemUtil.getBuilder(Head.SETTINGS_ENABLE)
                    .setName("§a" + language.getMessage("ENABLE"))
                    .setLore(language.getList( "ITEMS_LOBBY_ENABLE_LORE"))
                    .build());
            DISABLE_ITEM.put(lang, ItemUtil.getBuilder(Head.SETTINGS_DISABLE)
                    .setName("§c" + language.getMessage("DISABLE"))
                    .setLore(language.getList("ITEMS_LOBBY_DISABLE_LORE"))
                    .build());
        }
    }

    static {
        init();
    }
}
