package me.nekocloud.auth.manager;

import lombok.val;
import me.nekocloud.auth.objects.AuthPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import org.jetbrains.annotations.Nullable;
import pw.novit.nekocloud.bungee.api.gamer.BungeeGamer;
import pw.novit.nekocloud.bungee.api.utils.BungeeUtil;

import java.util.concurrent.TimeUnit;

public class FloodTask {

	private ScheduledTask loginFloodTask;

	public FloodTask(final @Nullable AuthPlayer authUser,
					 final BungeeGamer gamer,
					 final AuthManager manager
	) {
		val player = gamer.getPlayer();
		val lang = gamer.getLanguage();
		BungeeUtil.runTask(() -> {
			assert authUser != null;
			if (manager.hasPlayerAccount(gamer.getPlayerID()) && !authUser.isAuthorized()) {
				player.disconnect(lang.getMessage("AUTH_LOGIN_TIMEOUT"));
			} else {
				player.disconnect(lang.getMessage("AUTH_REGISTER_TIMEOUT"));
			}

			loginFloodTask.cancel();
		}, 2L, TimeUnit.MINUTES);

		loginFloodTask = BungeeUtil.runTask(() -> {
			if (manager.hasPlayerAccount(gamer.getPlayerID()) && authUser != null) {
				//gamer.sendActionBarLocale("AUTH_LOGIN_USAGE_BAR");
				gamer.sendMessageLocale("AUTH_LOGIN_USAGE");
			} else {
				//gamer.sendMessageLocale("AUTH_REGISTER_USAGE");
				gamer.sendActionBarLocale("AUTH_REGISTER_USAGE_BAR");
			}
		}, 0L, 2L, TimeUnit.SECONDS);
	}
}
