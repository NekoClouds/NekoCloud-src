package me.nekocloud.nekoapi.listeners;

import lombok.val;
import me.nekocloud.base.gamer.constans.SettingsType;
import me.nekocloud.nekoapi.loader.NekoAPI;
import me.nekocloud.nekoapi.utils.bukkit.PlayerUtil;
import org.bukkit.Effect;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class GameBloodListener extends DListener<NekoAPI> {

	public GameBloodListener(final NekoAPI nekoAPI) {
		super(nekoAPI);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent e) {
        if (e.getEntity().getType() != EntityType.PLAYER)
            return;

        val damager = PlayerUtil.getDamager(e.getEntity());
        if (damager == null)
            return;

        val gamer = GAMER_MANAGER.getGamer(damager);
		if (gamer == null)
			return;

        if (gamer.getSetting(SettingsType.BLOOD))
            e.getEntity().getWorld().playEffect(
					e.getEntity().getLocation().clone().add(-0.5, 1, -0.5),
                    Effect.STEP_SOUND, 50
			);
    }
}
