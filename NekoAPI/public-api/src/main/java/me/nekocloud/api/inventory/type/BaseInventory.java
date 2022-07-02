package me.nekocloud.api.inventory.type;

import me.nekocloud.api.player.BukkitGamer;
import org.bukkit.entity.Player;

public interface BaseInventory {

    /**
     * Открыть инвентарь
     * @param player - кому открыть
     */
    void openInventory(Player player);
    void openInventory(BukkitGamer gamer);

    /**
     * ВНИМАНИЕ! По умолчанию отменяется
     * отменять перемещение предметов в этом гуи или нет
     * PS перемещаться будут только те, что вне гуи
     * @param action - отменять или нет
     */
    void setDisableAction(boolean action);
    boolean isDisableAction();
}
