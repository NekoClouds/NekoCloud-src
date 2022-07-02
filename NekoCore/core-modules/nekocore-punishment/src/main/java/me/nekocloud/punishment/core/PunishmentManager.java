package me.nekocloud.punishment.core;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import lombok.val;
import me.nekocloud.base.sql.api.MySqlDatabase;
import me.nekocloud.core.CoreSql;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.chat.component.TextComponent;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.punishment.PunishmentType;
import me.nekocloud.punishment.data.BungeePunishmentAction;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author xwhilds
 */
public final class PunishmentManager {

    public static final PunishmentManager INSTANCE = new PunishmentManager();

    @Getter
    private final Multimap<String, Punishment> punishmentMap = HashMultimap.create();
    private final MySqlDatabase mySqlDatabase                = CoreSql.getDatabase();

    private static final String GET_PUNISHMENT_QUERY = "SELECT * FROM `punishment_data` WHERE `intruder_name`=? AND `Type`=?";
    private static final String DEL_PUNISHMENT_QUERY = "DELETE FROM `punishment_data` WHERE `intruder_name`=? AND `Type`=?";
    private static final String UPD_PUNISHMENT_QUERY = "INSERT INTO `punishment_data` VALUES (?, ?, ?, ?, ?)";

    private static final String GET_PUNISHMENT_HISTORY_QUERY = "SELECT * FROM `punishment_history` WHERE `intruder_name`=? AND `Type`=?";
    private static final String UPD_PUNISHMENT_HISTORY_QUERY = "INSERT INTO `punishment_history` VALUES (?, ?, ?, ?, ?)";

    /**
     * Выдать наказание игроку
     * Если нужно навсегда, то указывать
     * время -1.
     *
     * @param owner         кто выдал
     * @param intruder      нарушитель, который получил по жопе
     * @param type          тип
     * @param reason        причина
     * @param millisTime    время
     */
    public void givePunishmentToPlayer(@NotNull final CorePlayer owner,
                                       @NotNull final CorePlayer intruder,

                                       @NotNull final PunishmentType type,
                                       @NotNull final String reason,
                                       final long millisTime
    ) {
        if (hasPunishmentToPlayer(intruder.getName(), type))
            return;

        // Сохраняем в историю, записываем в бд и сразу же выдаем его
        savePunishmentAndHistory(owner, intruder, reason, type, millisTime).giveToPlayer(intruder);
    }

    /**
     * Снять бан c игрока
     *
     * @param intruder игрок
     */
    public void unbanPlayer(@NotNull final CorePlayer owner,
                            @NotNull final CorePlayer intruder
    ) {
        removePunishmentToData(
                owner.getName(),
                intruder.getName(),
                PunishmentType.PERMANENT_BAN);
        removePunishmentToData(
                owner.getName(),
                intruder.getName(),
                PunishmentType.TEMP_BAN);
    }

    /**
     * Снять блокировку чата у игрока
     *
     * @param intruder игрок
     */
    public void unmutePlayer(@NotNull final CorePlayer owner,
                             @NotNull final CorePlayer intruder
    ) {
        removePunishmentToData(
                owner.getName(),
                intruder.getName(),
                PunishmentType.PERMANENT_MUTE
        );
        removePunishmentToData(
                owner.getName(),
                intruder.getName(),
                PunishmentType.TEMP_MUTE
        );

        for (val bungeeServer : NekoCore.getInstance().getBungeeServers()) {
            bungeeServer.sendPacket(new BungeePunishmentAction.Clear(
                    intruder.getName(),
                    PunishmentType.TEMP_MUTE)
            );
        }
    }

    /**
     * Выкинуть игрока с сервера
     *
     * @param ownerPlayer    кто кикнул
     * @param intruderPlayer ник нарушителя
     * @param reason         причина кика
     */
    public void kickPlayer(@NotNull final CorePlayer ownerPlayer,
                           @NotNull final CorePlayer intruderPlayer,

                           @NotNull final String reason
    ) {
        intruderPlayer.disconnect(new TextComponent(
                "§5Вы были кикнуты с сервера!\n" +
                "\n" +
                "§7Кикнул: " + ownerPlayer.getDisplayName() + "\n" +
                "§7Причина: §f" + reason + "\n" +
                "\n" +
                "§7Если Вы считаете, что данное действие было совершено ошибочно,\n" +
                "§7то Вы можете обжаловать это на нашем форуме - §d§nforum.nekocloud.me"));
    }

    /**
     * Проверить игрока на наличие наказания определенного типа
     *
     * @param intruderName   ник игрока
     * @param punishmentType тип наказания
     */
    public boolean hasPunishmentToPlayer(@NotNull final String intruderName,
                                         @NotNull final PunishmentType punishmentType
    ) {
        return getPlayerPunishment(intruderName, punishmentType) != null;
    }

    /**
     * Получить текущее наказание игрока (при его наличии, естественно)
     *
     * @param intruderName   ник игрока
     * @param punishmentType тип наказания
     */
    public Punishment getPlayerPunishment(@NotNull final String intruderName,
                                          @NotNull final PunishmentType punishmentType
    ) {
        Punishment currentPunishment = punishmentMap.get(intruderName.toLowerCase())
                .stream()
                .filter(punishment -> punishment.getPunishmentType().equals(punishmentType)
                        && !punishment.isPunishmentExpired())

                .findFirst().orElse(null);

        if (currentPunishment == null) {
            Punishment punishment = mySqlDatabase.executeQuery(GET_PUNISHMENT_QUERY, resultSet -> {
                if (!resultSet.next())
                    return null;

                val punishmentOwner = resultSet.getString("owner_name");
                val punishmentReason = resultSet.getString("reason");
                val punishmentTime = resultSet.getTimestamp("time");
                val punishmentData = new Punishment(
                        intruderName.toLowerCase(),
                        punishmentOwner,
                        punishmentReason,
                        punishmentType,
                        punishmentTime == null ? -1 : punishmentTime.getTime());

                // Проверяем наказание, если истекло то удаляем
                if (punishmentData.isPunishmentExpired()) {
                    punishmentMap.remove(intruderName.toLowerCase(), punishmentData);

                    mySqlDatabase.execute(DEL_PUNISHMENT_QUERY,
                            intruderName,
                            punishmentType.ordinal());

                    return null;
                }

                return punishmentData;

                }, intruderName, punishmentType.ordinal());

            if (punishment != null) punishmentMap.put(intruderName.toLowerCase(), currentPunishment = punishment);
        }

        if (currentPunishment != null && currentPunishment.isPunishmentExpired()) {
            removePunishmentToData(
                    currentPunishment.getPunishmentIntruder(),
                    currentPunishment.getPunishmentOwner(),
                    currentPunishment.getPunishmentType());

            return null;
        }

        return currentPunishment;
    }

    /**
     * Получить историю наказаний игрока
     *
     * @param intruderName ник игрока
     */
    public Collection<Punishment> getPlayerPunishmentHistory(@NotNull final String intruderName) {
        return mySqlDatabase.executeQuery(GET_PUNISHMENT_HISTORY_QUERY, (resultSet) -> {
            final Collection<Punishment> punishmentCollection = new ArrayList<>();

            while (resultSet.next()) {
                val punishmentOwner = resultSet.getString("owner_name");
                val punishmentReason = resultSet.getString("reason");

                val punishmentType = PunishmentType.PUNISHMENT_TYPES[resultSet.getInt("type")];
                val punishmentTime = resultSet.getTimestamp("time");

                // добавляем в коллекцию
                punishmentCollection.add(new Punishment(
                        intruderName.toLowerCase(),
                        punishmentOwner,
                        punishmentReason,
                        punishmentType,
                        punishmentTime == null ? -1 : punishmentTime.getTime())
                );
            }

            return punishmentCollection;
        }, intruderName);
    }


    /**
     * Сохранить наказание и историю в базу данных
     *
     * @param ownerPlayer    кто выдал
     * @param intruderPlayer нарушитель, который получил по жопе
     * @param reason         причина
     * @param punishmentType тип
     * @param millisTime     время
     */
    private Punishment savePunishmentAndHistory(@NotNull final CorePlayer ownerPlayer,
                                                @NotNull final CorePlayer intruderPlayer,

                                                @NotNull final String reason,
                                                @NotNull final PunishmentType punishmentType,

                                                final long millisTime
    ) {
        val punishment = new Punishment(
                intruderPlayer.getName(),
                ownerPlayer.getName(),
                reason,
                punishmentType,
                millisTime >= 0 ? System.currentTimeMillis() + millisTime : -1);

        punishmentMap.put(intruderPlayer.getName().toLowerCase(), punishment);

        mySqlDatabase.execute(UPD_PUNISHMENT_HISTORY_QUERY,
                intruderPlayer.getName(),
                ownerPlayer.getName(),
                punishmentType.ordinal(),
                reason,
                millisTime > 0 ? new Timestamp(punishment.getPunishmentTime()) : null);

        mySqlDatabase.execute(UPD_PUNISHMENT_QUERY,
                intruderPlayer.getName(),
                ownerPlayer.getName(),
                punishmentType.ordinal(),
                reason,
                millisTime > 0 ? new Timestamp(punishment.getPunishmentTime()) : null);

        // возвращаем только что созданное наказание
        return punishment;
    }


    /**
     * Удалить наказание из бд текущих
     *
     * @param intruderName   ник нарушителя
     * @param type           тип
     */
    private void removePunishmentToData(@NotNull final String intruderName,
                                        @NotNull final String ownerName,

                                        @NotNull final PunishmentType type
    ) {

        // Получаем это наказание для проверки
        val punishment = getPlayerPunishment(intruderName, type);
        if (punishment == null)
            return;

        punishmentMap.remove(intruderName.toLowerCase(), punishment);

        // Удаляем наказание
        mySqlDatabase.execute(DEL_PUNISHMENT_QUERY,
                intruderName,
                type.ordinal());

        // Сохраняем в историю
        mySqlDatabase.execute(UPD_PUNISHMENT_HISTORY_QUERY,
                intruderName,
                ownerName,
                type.ordinal(),
                "clear_punishment228",
                System.currentTimeMillis());
    }

}
