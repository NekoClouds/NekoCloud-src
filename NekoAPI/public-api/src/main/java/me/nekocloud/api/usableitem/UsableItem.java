package me.nekocloud.api.usableitem;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface UsableItem {
    /**
     * Метод, который вернет настройку того,
     * что можно кликать по айтему в гуи или нет
     * @return - true or false
     */
    boolean isInvClick();

    /**
     * Метод, который вернет настройку того,
     * что можно выбрасывать айтем или нет
     * @return - true or false
     */
    boolean isDrop();

    /**
     * Метод, который вернет владельца айтема
     * если владелец не null, то при его выходе айтем
     * будет удаоен
     * @return - игрок или null (если владельца нет)
     */
    Player getOwner();

    /**
     * Метод, который вернет ItemStack
     * со всеми его настройками(Lore, Material И тд)
     * @return - ItemStack предмета
     */
    ItemStack getItemStack();

    /**
     * Получить ClickAction, который выполнится при клике по данному предмету
     * @return - ClickAction данного предмета
     */
    ClickAction getClickAction();

    /**
     * Включить или отключить кликанье этии предметом в инвентаре
     * по умолчанию true
     * (очень удобный клик по айтему в гуи)
     * @param invClick - true or false
     */
    void setInvClick(boolean invClick);

    /**
     * Включить или отключить выбрасыванье этого предмета игроком
     * по умолчанию false
     * @param drop - true or false
     */
    void setDrop(boolean drop);

    /**
     * Назначить владельца у данного айтема
     * по умолчанию null
     * если будет владелец, то айтем удалится
     * при выходе владельца
     * @param player - игрок владелец
     */
    void setOwner(Player player);

    /**
     * Полностью удалить айтем из
     * памяти и отключить его выполнение
     */
    void remove();
}
