package me.nekocloud.api.inventory.action;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public interface ClickAction {

    /**
     * Метод который будет вызван при клики по айтему в инв
     * @param clicker - кто кликнул
     * @param clickType - тип клика
     * @param slot - слот на который кликнули
     */
    void onClick(Player clicker, ClickType clickType, int slot);
}
