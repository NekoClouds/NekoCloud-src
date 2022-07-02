package me.nekocloud.packetlib.libraries.entity.customstand;

import me.nekocloud.api.entity.stand.CustomStand;
import me.nekocloud.api.event.gamer.GamerInteractCustomStandEvent;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.nekoapi.listeners.DListener;
import me.nekocloud.nekoapi.loader.NekoAPI;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import me.nekocloud.packetlib.libraries.entity.EntityAPIImpl;
import me.nekocloud.packetlib.packetreader.event.AsyncPlayerInUseEntityEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerRespawnEvent;

public class StandListener extends DListener<NekoAPI> {

    private final StandManager standManager;

    public StandListener(EntityAPIImpl entityAPI) {
        super(entityAPI.getNekoAPI());

        this.standManager = entityAPI.getStandManager();
    }

    @EventHandler
    public void onInteract(AsyncPlayerInUseEntityEvent e) {
        Player player = e.getPlayer();
        int entityId = e.getEntityId();

        CustomStand stand = standManager.getStand(entityId);
        if (stand == null) {
            return;
        }

        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null) {
            return;
        }

        GamerInteractCustomStandEvent event = new GamerInteractCustomStandEvent(gamer, stand,
                (e.getAction() == AsyncPlayerInUseEntityEvent.Action.ATTACK
                        ? GamerInteractCustomStandEvent.Action.LEFT_CLICK
                        : GamerInteractCustomStandEvent.Action.RIGHT_CLICK));
        BukkitUtil.runTask(() -> BukkitUtil.callEvent(event));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        standManager.getStands().values().forEach(stand -> stand.destroy(player));
    }
}
