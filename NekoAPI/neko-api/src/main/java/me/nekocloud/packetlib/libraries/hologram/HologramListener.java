package me.nekocloud.packetlib.libraries.hologram;

import me.nekocloud.api.entity.stand.CustomStand;
import me.nekocloud.api.event.gamer.GamerInteractCustomStandEvent;
import me.nekocloud.api.event.gamer.GamerInteractHologramEvent;
import me.nekocloud.api.event.gamer.async.AsyncGamerQuitEvent;
import me.nekocloud.api.hologram.Hologram;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.nekoapi.listeners.DListener;
import me.nekocloud.nekoapi.loader.NekoAPI;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class HologramListener extends DListener<NekoAPI> {

    private final HologramManager hologramManager;

    HologramListener(final HologramAPIImpl hologramAPI) {
        super(hologramAPI.getNekoAPI());

        this.hologramManager = hologramAPI.getHologramManager();
    }

    @EventHandler
    public void onPlayerInteractCustomStand(final GamerInteractCustomStandEvent e) {
        final CustomStand stand = e.getStand();
        final BukkitGamer gamer = e.getGamer();

        final GamerInteractCustomStandEvent.Action action = e.getAction();

        final Hologram hologram = hologramManager.getHologramByStand().get(stand);
        if (hologram == null)
            return;

        final GamerInteractHologramEvent event = new GamerInteractHologramEvent(gamer, hologram, action);
        BukkitUtil.callEvent(event);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(AsyncGamerQuitEvent e) {
        final Player player = e.getGamer().getPlayer();
        if (player == null) {
            return;
        }

        hologramManager.getHolograms().forEach(hologram -> {
            hologram.removeTo(player);
            if (hologram.getOwner() != null && player.getName().equals(hologram.getOwner().getName()))
                hologram.remove();
        });
    }
}
