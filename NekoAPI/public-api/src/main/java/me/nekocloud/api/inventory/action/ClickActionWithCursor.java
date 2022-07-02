package me.nekocloud.api.inventory.action;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public abstract class ClickActionWithCursor implements ClickAction {

    @Setter
    @Getter
    private ItemStack cursor;

    @Override
    public final void onClick(Player clicker, ClickType clickType, int slot) {
        cursor = onClick(clicker, clickType, cursor, slot);
    }

    /**
     * Метод который будет вызван при клики по айтему в инв
     * @param clicker - кто кликнул
     * @param clickType - тип клика
     * @param cursor - каким айтемом кликнули
     * @param slot - слот на который кликнули
     */
    public abstract ItemStack onClick(Player clicker, ClickType clickType, ItemStack cursor, int slot);
}
