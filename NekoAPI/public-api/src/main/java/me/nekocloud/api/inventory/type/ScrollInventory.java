package me.nekocloud.api.inventory.type;

import me.nekocloud.api.inventory.DItem;

import java.util.List;

public interface ScrollInventory extends BaseInventory {

    /**
     * добавить айтемы для скролла
     */
    void addItemScroll(DItem item);
    void addItemsScroll(List<DItem> items);

    /**
     * удалить какой-то айтем для скролла
     * @param numberItem - номер айтема
     */
    void removeItemScroll(int numberItem);

    /**
     * удалить все айтемы для скролла
     */
    void removeItemsScroll();

    /**
     * очистить инвентарь полностью
     */
    void clearInventory();

    /**
     * имя инвентаря
     * @return - имя
     */
    String getName();

    /**
     * добавить обычный айтем на опр слот(от 0 до 8 только)
     * @param slot - слот
     * @param item - айтем
     */
    void setItem(int slot, DItem item);
}
