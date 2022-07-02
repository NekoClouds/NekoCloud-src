package me.nekocloud.api.inventory.action;

import org.bukkit.entity.Player;

public interface InventoryAction {

    /**
     * выполняется при открытии инв
     * @param player - кто открыл
     */
    default void onOpen(Player player) {
        //хуйня
    }

    /**
     * выполняется при открытии инв
     * @param player - кто открыл
     */
    default void onClose(Player player) {
        //хуйня
    }
}
