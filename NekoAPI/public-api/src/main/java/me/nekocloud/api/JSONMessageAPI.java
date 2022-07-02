package me.nekocloud.api;

import me.nekocloud.base.util.JsonBuilder;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;

public interface JSONMessageAPI {

    /**
     * отослать игроку CraftJsonMessage
     * @param player - игрок
     * @param json - String которая содержит jsonСообщение
     */
    void send(Player player, String json);

    /**
     * Отправить красивое Json сообщение
     * @param player - кому слать
     * @param jsonBuilder - json
     */
    @Deprecated
    void send(Player player, JsonBuilder jsonBuilder);

    void send(Player player, BaseComponent... components);
}
