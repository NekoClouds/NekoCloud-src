package me.nekocloud.api.inventory;

import me.nekocloud.api.inventory.action.InventoryAction;
import me.nekocloud.api.inventory.type.DInventory;
import me.nekocloud.api.inventory.type.MultiInventory;
import me.nekocloud.api.inventory.type.ScrollInventory;
import me.nekocloud.base.locale.Language;
import org.bukkit.entity.Player;

import java.util.List;

public interface InventoryAPI {
    /**
     * Создать инвентарь
     * @param player - если он пишется, то инв чисто его
     * @param name - назв инвенторя
     * @param rows - кол-во строк
     * @param inventoryAction - действие при открытии или закрытии инв
     * @return DInventory
     */
    DInventory createInventory(Player player, String name, int rows, InventoryAction inventoryAction);
    DInventory createInventory(Player player, String name, int rows);
    DInventory createInventory(String name, int rows);
    DInventory createInventory(Player player, int rows, Language lang, String key, Object... objects);
    DInventory createInventory(int rows, Language lang, String key, Object... objects);

    /**
     * тоже самое, что и выше, но там лист инв будет
     */
    MultiInventory createMultiInventory(Player player, String name, int rows);
    MultiInventory createMultiInventory(String name, int rows);
    MultiInventory createMultiInventory(Player player, int rows, Language lang, String key, Object... objects);
    MultiInventory createMultiInventory(int rows, Language lang, String key, Object... objects);

    /**
     * скролл инвентарь аля хайп
     * @param lang - локализация для кнопок
     */
    ScrollInventory createScrollInventory(Player player, String name, Language lang);
    ScrollInventory createScrollInventory(String name, Language lang);

    /**
     * засетить кнопки в DInventory или MultiInventory
     * чтобы можно было переключаться между
     * страницами
     * @param lang - локализация
     * @param pagesCount - кол-во страниц
     * @param pages - лист страник DInventory
     * @param slotDown - слот кнопки назад
     * @param slotUp - слот кнопки вперед
     */
    void pageButton(Language lang, int pagesCount, List<DInventory> pages, int slotDown, int slotUp);
    void pageButton(Language lang, int pagesCount, MultiInventory inventory, int slotDown, int slotUp);
}
