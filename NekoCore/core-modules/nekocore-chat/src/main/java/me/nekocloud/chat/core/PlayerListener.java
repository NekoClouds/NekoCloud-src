package me.nekocloud.chat.core;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.chat.core.manager.IgnoreManager;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.event.EventHandler;
import me.nekocloud.core.api.event.EventListener;
import me.nekocloud.core.api.event.player.ConnectedEvent;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class PlayerListener implements EventListener {

	CoreChat module;
	IgnoreManager manager;

	@EventHandler
	public void onJoin(ConnectedEvent e) {
		val player = e.getCorePlayer();

        NekoCore.getInstance().getTaskScheduler().runAsync(module, () ->
						player.addData("ignore_list", manager.getIgnoreList(player.getPlayerID()))
		);
	}
}
