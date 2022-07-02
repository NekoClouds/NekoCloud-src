package me.nekocloud.api.usableitem;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface UsableAPI {

    /**
     * Создать usableItem
     * @param itemStack - айтем стак
     * @param owner - владелец айтема(когда он выйдет, айтем удалится)
     * @param clickAction - действие при клике по предмету
     */
    UsableItem createUsableItem(ItemStack itemStack, Player owner, ClickAction clickAction);
    UsableItem createUsableItem(ItemStack itemStack, ClickAction clickAction);

    /**
     * удалить из памяти
     * @param item - айтем
     */
    void removeItem(UsableItem item);

    /**
     * получить все айтемы
     * @return - айтемы
     */
    List<UsableItem> getUsableItems();
}
