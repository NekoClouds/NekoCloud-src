package me.nekocloud.auth.listeners;

import com.google.common.collect.ImmutableSet;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.auth.BungeeAuth;
import me.nekocloud.auth.core.AuthAction;
import me.nekocloud.auth.core.AuthData;
import me.nekocloud.auth.manager.AuthManager;
import me.nekocloud.auth.manager.FloodTask;
import me.nekocloud.auth.utils.RedirectUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.event.EventHandler;
import pw.novit.nekocloud.bungee.api.event.gamer.AsyncGamerLoginEvent;
import pw.novit.nekocloud.bungee.api.event.player.DPreLoginEvent;
import pw.novit.nekocloud.bungee.api.gamer.BungeeGamer;
import pw.novit.nekocloud.bungee.api.utils.BungeeUtil;
import pw.novit.nekocloud.bungee.listeners.ProxyListener;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthListener extends ProxyListener<BungeeAuth> {

	AuthManager AUTH_MANAGER;
	BaseComponent dataError = new TextComponent(
			"""
			Ошибка при загрузке данных
			Сообщите администрации: vk.com/nekocloud
			""");
	ImmutableSet<String> availableCommands = ImmutableSet.of(
            "/login", "/l", "/логин", "/дщгин",
			"/reg", "/register", "/рег",  "/регистрация"
    );

	public AuthListener(BungeeAuth plugin) {
		super(plugin);

		AUTH_MANAGER = plugin.getAuthManager();
	}

	@EventHandler
	public void onLicenseLogin(final DPreLoginEvent e) {
		val connection = e.getConnection();
		val name = connection.getName();

		e.registerIntent(plugin);
		BungeeUtil.submitAsync(() -> {

//			authManager.loadLicenseData(name);
//			if (authManager.isWaitLicense(name)) {
//				connection.setOnlineMode(true);
//				authManager.removeWaitLicense(name);
//			}

			e.completeIntent(plugin);
		});
	}

	@EventHandler
	public void onGamerLogin(final AsyncGamerLoginEvent e) {
		val gamer = e.getGamer();
		BungeeUtil.submitAsync(() -> AUTH_MANAGER.loadOrCreate(gamer.getPlayerID()));
	}

	@EventHandler
	public void onConnect(final ServerConnectEvent e) {
		val player = e.getPlayer();
		val connection = e.getPlayer().getPendingConnection();
		val gamer = BungeeGamer.getGamer(player.getName());
		if (gamer == null) {
			player.disconnect(dataError);
			return;
		}

		val lang = gamer.getLanguage();
		val authPlayer = AUTH_MANAGER.loadOrCreate(gamer.getPlayerID());

		// Отклоняем ивент если игрок как-то оказывается на другом сервере(например хаб)
		if (player.getServer() != null) {
            if (authPlayer == null || (!authPlayer.isAuthorized() && !e.getTarget().getName().startsWith("auth")))
                e.setCancelled(true);
            return;
        }

		if (authPlayer == null || !authPlayer.isAuthorized()) {
            val login = RedirectUtil.getBestAuth();
            if (login == null) {
                player.disconnect(dataError);
                return;
            }

            e.setTarget(login);

			new FloodTask(authPlayer, gamer, AUTH_MANAGER);
            return;
        }

		if (connection.isOnlineMode()) {
			plugin.sendPacket(new AuthData(authPlayer.getPlayerID(), AuthAction.WAIT_COMPLETE));
			return;
		}

		plugin.sendPacket(new AuthData(authPlayer.getPlayerID(), AuthAction.WAIT_2FA_CODE));
	}

	@EventHandler(priority = -64)
	public void onChat(final ChatEvent e) {
		if (!(e.getSender() instanceof ProxiedPlayer player))
			return;

		// чтобы не делать вечные проверки ниже
		val serverInfo = player.getServer().getInfo();
		if (!RedirectUtil.AUTH_SERVERS.contains(serverInfo.getName()))
			return;

		val command = e.getMessage().split(" ")[0].trim().toLowerCase();
		if (availableCommands.contains(command))
			return;

		val gamer = BungeeGamer.getGamer(player.getName());
		val authUser = AUTH_MANAGER.loadOrCreate(gamer.getPlayerID());
		if (authUser == null || !authUser.isAuthorized()) {
			gamer.sendMessageLocale("");
			e.setCancelled(true);
		}
	}
}
