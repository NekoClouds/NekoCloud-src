package me.nekocloud.punishment.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.TimeUtil;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.chat.component.TextComponent;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.core.api.event.player.PlayerLoginEvent;
import me.nekocloud.punishment.PunishmentType;
import me.nekocloud.punishment.data.BungeePunishmentAction;
import org.jetbrains.annotations.NotNull;

import static lombok.AccessLevel.PRIVATE;

/**
 * @author xwhilds
 */
@Getter
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public final class Punishment {

    String punishmentIntruder;
    String punishmentOwner;
    String punishmentReason;

    PunishmentType punishmentType;
    long punishmentTime;

    /**
     * Проверить, истек ли срок наказания
     */
    public boolean isPunishmentExpired() {
        return punishmentTime > 0 && System.currentTimeMillis() > punishmentTime;
    }

    public boolean isPermanent() {
        return punishmentTime < 0;
    }

    /**
     * Вызывается при входе игрока,
     * тем самым выдает ему наказание
     *
     * @param event loginEvent
     */
    public void giveOnLogin(final PlayerLoginEvent event) {
        val offlinePlayerOwner = NekoCore.getInstance().getOfflinePlayer(punishmentOwner);
        val time = punishmentTime - System.currentTimeMillis();
        val lang = Language.DEFAULT;
        switch (punishmentType) {
            case TEMP_BAN -> {
                if (isPunishmentExpired())
                    return;

                event.setCancelReason(
                            "§d§lNEKO§f§lCLOUD" +
                            "\n" +
                            "§cВаш аккаунт временно заблокирован!\n" +
                            "§cРазблокировка через: " + "\n" +
                            "§c" + TimeUtil.leftTime(lang, time) + "\n" +
                            "\n" +
                            "§fЗаблокировал: " + offlinePlayerOwner.getDisplayName() + "\n" +
                            "§fПричина: §7" + punishmentReason + "\n" +
                            "\n" +
                            "\n" +
                            "§7Обжаловать наказание можно на форуме:\n" +
                            "§dforum.NekoCloud.me"
                );
                event.setCancelled(true);
            }

            case PERMANENT_BAN -> {
                event.setCancelReason(
                             "§d§lNEKO§f§lCLOUD" +
                             "\n" +
                             "§cВаш аккаунт заблокирован §lНАВСЕГДА!\n" +
                             "\n" +
                             "§fЗаблокировал: " + offlinePlayerOwner.getDisplayName() + "\n" +
                             "§fПричина: §f" + punishmentReason + "\n" +
                             "\n" +
                             "\n" +
                             "§7Обжаловать наказание можно на форуме:\n" +
                             "§dforum.NekoCloud.me"
                );
                event.setCancelled(true);

            }

            case TEMP_MUTE -> {
                if (isPunishmentExpired()) {
                    return;
                }

                issuePunishment(
                        offlinePlayerOwner.getName(),
                        event.getPlayerName(),
                        time
                );
            }

            case PERMANENT_MUTE -> {
                issuePunishment(
                        offlinePlayerOwner.getName(),
                        event.getPlayerName(),
                        -1
                );
            }
        }
    }

    /**
     * Выдать наказание
     *
     * @param corePlayer кому выдать
     */
    public void giveToPlayer(@NotNull final CorePlayer corePlayer) {
        val offlinePlayerOwner = NekoCore.getInstance().getOfflinePlayer(punishmentOwner);
        val time = punishmentTime - System.currentTimeMillis();
        val lang = corePlayer.getLanguage();
        switch (punishmentType) {
            case TEMP_BAN -> {
                if (isPunishmentExpired()) {
                    return;
                }

                corePlayer.disconnect(new TextComponent(
                            "§d§lNEKO§f§lCLOUD" +
                            "\n" +
                            "§cВаш аккаунт временно заблокирован!\n" +
                            "§cРазблокировка через: " + "\n" +
                            "§c" + TimeUtil.leftTime(lang, time) + "\n" +
                            "\n" +
                            "§fЗаблокировал: " + offlinePlayerOwner.getDisplayName() + "\n" +
                            "§fПричина: §7" + punishmentReason + "\n" +
                            "\n" +
                            "\n" +
                            "§7Обжаловать наказание можно на форуме:\n" +
                            "§dforum.NekoCloud.me"
                ));
            }

            case PERMANENT_BAN ->
                corePlayer.disconnect(new TextComponent(
                        "§d§lNEKO§f§lCLOUD" +
                        "\n" +
                        "§cВаш аккаунт заблокирован §lНАВСЕГДА!\n" +
                        "\n" +
                        "§fЗаблокировал: " + offlinePlayerOwner.getDisplayName() + "\n" +
                        "§fПричина: §f" + punishmentReason + "\n" +
                        "\n" +
                        "\n" +
                        "§7Обжаловать наказание можно на форуме:\n" +
                        "§dforum.NekoCloud.me"
                ));

            case TEMP_MUTE -> {
                if (isPunishmentExpired())
                    return;

                corePlayer.sendMessageLocale("YOU_ARE_MUTED",
                        TimeUtil.leftTime(lang, time),
                        offlinePlayerOwner.getDisplayName());

                issuePunishment(
                        offlinePlayerOwner.getName(),
                        corePlayer.getName(),
                        time
                );
            }

            case PERMANENT_MUTE -> {
                corePlayer.sendMessageLocale("YOU_ARE_MUTED",
                        TimeUtil.leftTime(lang, time),
                        offlinePlayerOwner.getDisplayName());

                issuePunishment(
                        offlinePlayerOwner.getName(),
                        corePlayer.getName(),
                        -1
                );
            }
        }
    }

    private void issuePunishment(final String ownerName,
                                 final String intruderName,
                                 final long time
    ) {
        for (val bungee : NekoCore.getInstance().getBungeeServers())
            bungee.sendPacket(new BungeePunishmentAction.Issue(
                    ownerName,
                    intruderName,
                    punishmentReason,
                    punishmentType,
                    time
            ));
    }
}
