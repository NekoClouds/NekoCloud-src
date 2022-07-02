package me.nekocloud.punishment.core.listener;

import lombok.val;
import me.nekocloud.core.api.event.EventHandler;
import me.nekocloud.core.api.event.EventListener;
import me.nekocloud.core.api.event.player.PlayerLoginEvent;
import me.nekocloud.punishment.PunishmentType;
import me.nekocloud.punishment.core.PunishmentManager;

/**
 * @author xwhilds
 */
public final class PunishmentListener implements EventListener {

	private final PunishmentManager manager = PunishmentManager.INSTANCE;

	@EventHandler
    public void onPlayerLogin(final PlayerLoginEvent event) {
        val name = event.getPlayerName();

        val tempBan = manager.getPlayerPunishment(name,
				PunishmentType.TEMP_BAN);
        val permanentBan = manager.getPlayerPunishment(name,
				PunishmentType.PERMANENT_BAN);

		val tempMute = manager.getPlayerPunishment(name,
				PunishmentType.TEMP_MUTE);
		val permanentMute = manager.getPlayerPunishment(name,
				PunishmentType.PERMANENT_MUTE);

        if (tempBan != null)
            tempBan.giveOnLogin(event);
        else if (permanentBan != null)
            permanentBan.giveOnLogin(event);
        else if (tempMute != null)
			tempMute.giveOnLogin(event);
		else if (permanentMute != null)
			permanentMute.giveOnLogin(event);
    }
}
