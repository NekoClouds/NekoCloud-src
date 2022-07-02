package pw.novit.nekocloud.bungee.listeners;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.base.game.GameType;
import me.nekocloud.base.gamer.GamerAPI;
import me.nekocloud.base.gamer.sections.BaseSection;
import me.nekocloud.base.locale.Language;
import me.nekocloud.base.util.ExpireList;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import pw.novit.nekocloud.bungee.NekoBungeeAPI;
import pw.novit.nekocloud.bungee.api.event.gamer.AsyncGamerLoginEvent;
import pw.novit.nekocloud.bungee.api.event.gamer.AsyncGamerQuitEvent;
import pw.novit.nekocloud.bungee.api.event.gamer.GamerChangeGroupEvent;
import pw.novit.nekocloud.bungee.api.event.player.DPreLoginEvent;
import pw.novit.nekocloud.bungee.api.gamer.BungeeEntity;
import pw.novit.nekocloud.bungee.api.gamer.BungeeGamer;
import pw.novit.nekocloud.bungee.api.utils.BungeeUtil;
import pw.novit.nekocloud.bungee.api.utils.RedirectUtil;
import pw.novit.nekocloud.bungee.api.utils.SkinUtil;
import pw.novit.nekocloud.bungee.gamer.BungeeEntityManager;
import pw.novit.nekocloud.bungee.gamer.BungeeGamerImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static lombok.AccessLevel.PRIVATE;
import static net.md_5.bungee.api.event.ServerKickEvent.Cause.LOST_CONNECTION;
import static net.md_5.bungee.event.EventPriority.LOW;
import static net.md_5.bungee.event.EventPriority.LOWEST;

/**
 * #КодНовита
 */
@FieldDefaults(level = PRIVATE, makeFinal = true)
public final class GamerListener extends ProxyListener<NekoBungeeAPI> {

    // Геймеры, которые находятся в процессе инициализации
    Object2ObjectMap<String, BungeeGamerImpl> gamers2 = new Object2ObjectOpenHashMap<>();
    Map<String, BungeeGamerImpl> gamers = new ConcurrentHashMap<>();
    ExpireList<String> bannedIps        = new ExpireList<>();
    BungeeEntity bungee                 = BungeeEntityManager.getBungee();

    public GamerListener(final NekoBungeeAPI nekoBungeeAPI) {
        super(nekoBungeeAPI);
    }

    @EventHandler(priority = LOWEST)
    public void onPreLogin(final @NotNull PreLoginEvent event) {
        if (event.isCancelled())
            return;

        val connection = event.getConnection();
        val name = connection.getName();

        val regex = "^[a-zA-Z0-9_]+$";
        if (connection.getName().length() < 3 || connection.getName().length() > 16) {
            event.setCancelled(true);
            event.setCancelReason(new TextComponent(
                    """
                    §d§lNeko§l§fCloud\s

                    §fДлина никнейма должна быть не меньше 3 символов и не больше 16
                    """
            ));
            return;
        } else if (name.trim().contains(" ") || !name.matches(regex)) {
            event.setCancelled(true);
            event.setCancelReason(new TextComponent(
                    """
                    §d§lNeko§l§fCloud\s

                    §fВ нике есть недопустимые символы!
                    §7Разрешено использовать только цифры и английские буквы!
                    """
            ));
            return;
        }

        if (checkIp(event) && checkWl(event)) {
            event.registerIntent(plugin);
            BungeeUtil.submitAsync(() -> {
                Callback<DPreLoginEvent> callback = (result, throwable) -> {
                    // nothing :(
                };

                BungeeUtil.callEventAsync(new DPreLoginEvent(name, connection, callback));
                event.completeIntent(plugin);
            });
        }
    }

    // Чекаем ип для защиты админов
    private boolean checkIp(final @NotNull PreLoginEvent e) {
        val connection = e.getConnection();
        val ip = connection.getAddress().getAddress();

        val lang = Language.DEFAULT;
        if (bannedIps.contains(ip.toString())) {
            e.setCancelled(true);
            e.setCancelReason(new TextComponent(lang.getMessage("BUNGEE_IP_BLOCKED")));
            return false;
        }

        val name = connection.getName().toLowerCase();
        val checkIp = plugin.getIpProtect().getProtect().get(name);
        if (checkIp != null && !checkIp.equalsIgnoreCase(ip.toString())) {
            e.setCancelled(true);
            e.setCancelReason(new TextComponent(lang.getMessage("BUNGEE_ACCOUNT_DOES_NOT_BELONG")));
            return false;
        }

        return true;
    }

    // Чекаем вайтлист
    private boolean checkWl(final @NotNull PreLoginEvent e) {
        val connection = e.getConnection();
        val name = connection.getName();

        val lang = Language.DEFAULT;
        if (plugin.getWhitelistManager().isEnable()
                && !plugin.getWhitelistManager().getPlayerNames().contains(name.toLowerCase())) {
            e.setCancelReason(new TextComponent(plugin.getWhitelistManager().getKickMessage(lang)));
            e.setCancelled(true);

            bungee.sendMessage("§fИгрок §b" + name +
                    "§f попытался зайти, но был исключен т.к включены тех. работы");
            return false;
        }

        return true;
    }

    // Инициализация игрока в API
    @EventHandler(priority = LOWEST)
    public void onLogin(final @NotNull LoginEvent event) {
        val connection = event.getConnection();
        val name = connection.getName();

        GamerAPI.removeOfflinePlayer(name);

        event.registerIntent(plugin);
        BungeeUtil.submitAsync(() -> {

            val gamer = BungeeGamer.getOrCreate(name, connection.getAddress().getAddress());
            final Callback<AsyncGamerLoginEvent> callback = (asyncGamerLoginEvent, throwable) -> {

                // устанавливаем скин
                SkinUtil.setSkin(connection, gamer.getSkin(), null);
            };

            BungeeUtil.callEventAsync(new AsyncGamerLoginEvent(gamer, connection, callback));
            event.completeIntent(plugin);
        });
    }

    // Подгружаем геймера в мапу для проверки
    @EventHandler(priority = LOWEST)
    public void onLoadGamer(final @NotNull AsyncGamerLoginEvent event) {
        event.registerIntent(plugin);
        BungeeUtil.submitAsync(() -> {
            val gamer = (BungeeGamerImpl) event.getGamer();

            bungee.sendMessage("§fДанные игрока §b" + gamer.getName() + "§f загружены за (§b"
                    + (System.currentTimeMillis() - gamer.getStart()) + "ms§f)");

            gamers2.put(gamer.getName().toLowerCase(), gamer);
            event.completeIntent(plugin);
        });
    }

    // Окончательная проверка и инициализация геймера
    @EventHandler(priority = LOW)
    public void onLoginGamer(final @NotNull AsyncGamerLoginEvent event) {
        event.registerIntent(plugin);
        BungeeUtil.submitAsync(() -> {
            val connection = event.getConnection();
            val name = connection.getName();

            val bungeeGamer = gamers2.remove(name.toLowerCase());
            if (bungeeGamer == null) {
                event.setCancelReason(new TextComponent(
                        """
                        §d§lNeko§f§lCloud§r

                        §fОшибка при загрузке данных
                        §fСообщите нам: §dvk.me/nekocloud
                        """));
                event.setCancelled(true);
            }
            event.completeIntent(plugin);
        });
    }

    @EventHandler(priority = LOW)
    public void onServerConnect(final @NotNull ServerConnectedEvent event) {
        val player = event.getPlayer();
        val connection = player.getPendingConnection();
        val target = event.getServer();
        val gamer = BungeeGamer.getGamer(player.getName());
        BungeeUtil.submitAsync(()-> {
            SkinUtil.setSkin(connection, gamer.getSkin(), target);
        });
    }

    @EventHandler(priority = LOWEST)
    public void onPlayerDisconnect(final @NotNull PlayerDisconnectEvent event) {
        val player = event.getPlayer();
        val connection = player.getPendingConnection();

        val gamer = BungeeGamer.getGamer(player.getName());
        if (gamer != null) {
            final Callback<AsyncGamerQuitEvent> callback = (result, throwable) -> {

                //Удаляем геймера из кеша при выходе и сохраняем в бд последнюю инфу
                gamer.disconnect();
            };

            BungeeUtil.callEventAsync(new AsyncGamerQuitEvent(gamer, connection, callback));
        }
    }

    @EventHandler
    public void onServerKick(final @NotNull ServerKickEvent e) { // понимаю что пиздец, но мне лень делать ок
        val reason = BaseComponent.toLegacyText(e.getKickReasonComponent());
        val kickedFrom = e.getKickedFrom();
        val player = e.getPlayer();
        val gamer = BungeeGamer.getGamer(player.getName());
        if (gamer == null || e.getCause() == LOST_CONNECTION)
            return;

        val limboServer = RedirectUtil.bestServer("limbo");
        if (reason.contains("idle") || kickedFrom == null) {
            e.setCancelServer(limboServer);
            e.setCancelled(true);

        } else {
            // Если сервер является аутхом/лимбо то кикаем игрока.
            if (GameType.isTyped(kickedFrom.getName(), GameType.AUTH)
                    || GameType.isTyped(kickedFrom.getName(), GameType.LIMBO)) {
                return;
            }

            val cancelServer = e.getCancelServer();
            // Получаем серв на который кикаем игрока
            val lobbyServer = RedirectUtil.getFallbackLobby(kickedFrom.getName());
            if (lobbyServer != null) {
                e.setCancelServer(lobbyServer);
                e.setCancelled(true);
                gamer.sendMessageLocale("SERVER_WAS_DOWN",
                        kickedFrom.getName(), lobbyServer.getName(), reason
                );
                return;
            }

            if (cancelServer != null) {
                gamer.sendMessageLocale("SERVER_TO_LIMBO",
                        kickedFrom.getName(), reason
                );
            }
        }
    }

    @EventHandler
    public void onChangeGroup(final @NotNull GamerChangeGroupEvent e) {
        val gamer = e.getGamer();
        if (gamer == null)
            return;

        gamer.getSection(BaseSection.class).setGroup(e.getGroup(), false);

    }

}