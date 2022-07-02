package me.nekocloud.api.usableitem;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public interface ClickAction {

    /**
     * Метод, который будет вызван при клике по UsableItem
     * @param player игрок
     * @param clickType тип клика
     */
    void onClick(Player player, ClickType clickType, @Nullable Block clickedBlock);
}
