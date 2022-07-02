package me.nekocloud.api.scoreboard;

import me.nekocloud.api.depend.PacketObject;
import org.bukkit.entity.Player;

import java.util.Map;

public interface Objective extends PacketObject {

    /**
     * Что показывать в качестве борда
     */
    void setDisplayName(String displayName);

    /**
     * где именно будет показываться надпись
     * @param displaySlot - где именно
     */
    void setDisplaySlot(DisplaySlot displaySlot);

    /**
     * поставить или удалить очки
     * @param player - кому
     * @param score - очки
     */
    void setScore(Player player, int score);

    /**
     * получить список игроков(их ники) и
     * очки которые у них есть и которые
     * хранит данный objective
     */
    Map<String, Integer> getScores();

    /**
     * удалить очки скор определенного игрока
     * @param name - игрок
     */
    void removeScore(String name);
    void removeScore(Player player);
}
