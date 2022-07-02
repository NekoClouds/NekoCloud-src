package me.nekocloud.api.scoreboard;

import me.nekocloud.api.player.BukkitGamer;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;

public interface Board {
    void showTo(Player player);
    void showTo(BukkitGamer gamer);

    Map<Integer, BoardLine> getLines();
    int getSize();

    @Nullable
    Player getOwner();

    void setDisplayName(String name);
    void setDynamicDisplayName(String name); //фирменная анимация
    void setDynamicDisplayName(Collection<String> lines, long speed);

    /*
    в notChangeText статичный текст, а в change тот, что должен меняться
    так борд не будет мигать
     */
    void setDynamicLine(int number, String notChangeText, String change);
    void setLine(int number, String text);

    /*
    для меняющейся строчки
     */
    void updater(Runnable runnable, long speed);
    void updater(Runnable runnable);
    void removeUpdater(Runnable runnable);

    void removeLine(int number);

    void remove();

}
