package me.nekocloud.api.inventory.type;

import me.nekocloud.api.inventory.DItem;
import org.bukkit.entity.Player;

import java.util.List;

public interface MultiInventory extends BaseInventory {

    /**
     * Открыть инвентарь
     * @param player - кому открыть
     * @param page - какую страницу открыть (по умолчанию 1ю)
     */
    void openInventory(Player player, int page);

    /**
     * добавить айтем
     * @param page - страница
     * @param slot - куда сетить айтем
     * @param item - DItem с действием
     */
    void setItem(int page, int slot, DItem item);
    void addItem(int page, DItem dItem);

    /**
     * засетить этот айтем на все слоты
     */
    void setItem(int slot, DItem item);

    /**
     * удалить айтем по слот
     * @param page - страница
     * @param slot - айтем
     */
    void removeItem(int page, int slot);

    /**
     * удалить страницу из памяти
     * @param page - номер страницы
     */
    void removePage(int page);

    /**
     * очистить все интенватри (кроме кнопок вперед/назад)
     */
    void clearInventories();

    /**
     * имя инвентаря
     * @return - имя
     */
    String getName();

    /**
     * кол-во слотов в инв
     * @return - кол-во слотов в инв
     */
    int size();

    /**
     * кол-во страниц
     * @return - число страниц
     */
    int pages();

    /**
     * получить список Dинвентарей
     * @return список инвентарей
     */
    List<DInventory> getInventories();
}
