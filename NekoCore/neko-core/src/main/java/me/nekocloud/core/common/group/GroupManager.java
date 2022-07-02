package me.nekocloud.core.common.group;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.val;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.base.sql.GlobalLoader;
import me.nekocloud.core.CoreSql;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.core.api.event.player.GroupChangeEvent;
import me.nekocloud.core.common.NetworkManager;
import me.nekocloud.core.io.packet.bukkit.BukkitGroupPacket;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

// TODO _Novit_: переписать это говнище
public final class GroupManager {

    public static final GroupManager INSTANCE = new GroupManager();

    private final Int2ObjectMap<Group> playerGroupMap = new Int2ObjectOpenHashMap<>();

    /**
     * Установить новую группу игроку
     *
     * @param corePlayer игрок
     * @param group      новая группа
     */
    public void setGroupToPlayer(
            final @NotNull CorePlayer corePlayer,
            final @NotNull Group group
    ) {
        callEvent(corePlayer.getName(), group, corePlayer.getGroup());

        playerGroupMap.put(corePlayer.getPlayerID(), group);
        corePlayer.setGroup(group);
        if (corePlayer.isOnline()) {
            corePlayer.setGroup(group, false);
            sendUpdateStatusPacket(corePlayer.getPlayerID(), group);
        } else {
            corePlayer.setGroup(group, true);
        }
    }

    /**
     * Установить новую группу игроку
     * отправив пакет на баккит сервера
     *
     * @param playerName ник игрока
     * @param group      новая группа
     */
    public void setGroupToPlayer(
            final @NotNull String playerName,
            final @NotNull Group group
    ) {
        val corePlayer = NekoCore.getInstance().getOfflinePlayer(playerName);
        callEvent(playerName, group, getPlayerGroup(playerName));

        playerGroupMap.put(corePlayer.getPlayerID(), group);
        if (corePlayer.isOnline()) {
            corePlayer.setGroup(group, false);
            sendUpdateStatusPacket(corePlayer.getPlayerID(), group);
        } else {
            corePlayer.setGroup(group, true);
        }
    }

    /**
     * Получить группу игрока по его ид
     * @param playerId ид игрока
     */
    public Group getPlayerGroup(final int playerId) {
        Group group = playerGroupMap.get(playerId);
        if (group == null) {
            group = GlobalLoader.getGroup(playerId);

            if (group != null) {
                playerGroupMap.put(playerId, group);
            }
        }

        return group;
    }

    /**
     * Получить группу игрока по его нику
     * @param playerName ник игрока
     */
    public Group getPlayerGroup(final @NotNull String playerName) {
        return getPlayerGroup(NetworkManager.INSTANCE.getPlayerID(playerName));
    }

    private final Cache<Group, Collection<CorePlayer>> offlinePlayersByGroupCache = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build();

    /**
     * Получить список оффлайн игроков по группе
     *
     * @param group - группа
     */
    public Collection<CorePlayer> getOfflinePlayersByGroup(final @NotNull Group group) {
        offlinePlayersByGroupCache.cleanUp();

        Collection<CorePlayer> offlinePlayersCollection = offlinePlayersByGroupCache.asMap().get(group);
        if (offlinePlayersCollection == null) {

            offlinePlayersCollection = CoreSql.getDatabase().executeQuery("SELECT * FROM `players_groups` WHERE `group_id`=?",
                    resultSet -> {

                        Collection<CorePlayer> corePlayerCollection = new ArrayList<>();
                        while (resultSet.next()) {

                            int playerId = resultSet.getInt("Id");
                            String playerName = NetworkManager.INSTANCE.getPlayerName(playerId);

                            corePlayerCollection.add(NekoCore.getInstance().getOfflinePlayer(playerName));
                        }

                        return corePlayerCollection;
                    }, group.getId());

            offlinePlayersByGroupCache.put(group, offlinePlayersCollection);
        }

        return offlinePlayersCollection;
    }

    private void sendUpdateStatusPacket(
            final int playerID,
            final Group group
    ) {
        val groupUpdatePacket = new BukkitGroupPacket(playerID, group.getLevel());
        for (val bukkitServer : NekoCore.getInstance().getServerManager().getBukkitServers().values()) {
            bukkitServer.sendPacket(groupUpdatePacket);
        }
    }

    private void callEvent(
            final @NotNull String playerName,
            final @NotNull Group currentGroup,
            final @NotNull Group previousGroup
    ) {
        val playerGroupChangeEvent = new GroupChangeEvent(NekoCore.getInstance().
                getOfflinePlayer(playerName),
                currentGroup,
                previousGroup);

        NekoCore.getInstance().callEvent(playerGroupChangeEvent);
    }
}
