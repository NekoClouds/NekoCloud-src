package pw.novit.nekocloud.bungee.api.gamer;

import lombok.val;
import me.nekocloud.base.gamer.GamerAPI;
import me.nekocloud.base.gamer.GamerBase;
import me.nekocloud.base.gamer.OnlineGamer;
import me.nekocloud.base.locale.Language;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import pw.novit.nekocloud.bungee.gamer.BungeeGamerImpl;

import java.net.InetAddress;
import java.util.Map;

public interface BungeeGamer extends BungeeEntity, OnlineGamer {

    static BungeeGamer getGamer(final @NotNull ProxiedPlayer player) {
        return getGamer(player.getName());
    }

    static BungeeGamer getGamer(final @NotNull String name) {
        return (BungeeGamer) GamerAPI.getByName(name);
    }

    static BungeeGamer getGamer(final int id) {
        return (BungeeGamer) GamerAPI.getGamers().values()
                .stream()
                .filter(gamerBase -> gamerBase.getPlayerID() == id)
                .findFirst()
                .orElse(null);
    }

    static Map<String, GamerBase> getGamers() {
        return GamerAPI.getGamers();
    }

    /**
     * Создать игрока в апи
     * @param name ник
     * @param inetAddress его ип адрес при входе
     */
    static @NotNull BungeeGamer getOrCreate(
            @NotNull final String name,
            @NotNull final InetAddress inetAddress
    ) {
        val gamer = (BungeeGamer) GamerAPI.getByName(name);
        if (gamer != null)
            return gamer;

        return new BungeeGamerImpl(name, inetAddress);
    }

    /**
     * Получить игрока
     */
    ProxiedPlayer getPlayer();

    /**
     * Обновить скин игрока
     */
    void updateSkin(String skinName);

    /**
     * Отправить тайтл игроку
     */
    void sendTitle(Title title);

    /*
    были ли изменены данные игрока или нет
     */
    boolean isSaved();
    void setSaved(boolean saved);

    InetAddress getLastIp();
    String getLastServer();

    boolean isCoreLogged();
    void setCoreLogged(boolean logged);

    /**
     * Установить язык игроку
     * @param language язык который нужно
     */
    void setLanguage(final Language language);

    /**
     * переписать всю инфу об игроке в БД
     */
    void disconnect();

    void disconnect(String reason);
}
