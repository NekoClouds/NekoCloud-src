package me.nekocloud.packetlib.libraries.entity.npc;

import me.nekocloud.api.entity.npc.NPC;
import me.nekocloud.api.event.gamer.GamerInteractNPCEvent;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.nekoapi.listeners.DListener;
import me.nekocloud.nekoapi.loader.NekoAPI;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import me.nekocloud.packetlib.libraries.entity.EntityAPIImpl;
import me.nekocloud.packetlib.packetreader.event.AsyncPlayerInUseEntityEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

public class NPCListener extends DListener<NekoAPI> {

    private final NPCManager npcManager;

    public NPCListener(EntityAPIImpl entityAPI) {
        super(entityAPI.getNekoAPI());

        this.npcManager = entityAPI.getNpcManager();
    }

    @EventHandler
    public void onInterractNPC(AsyncPlayerInUseEntityEvent e) {
        Player player = e.getPlayer();
        int entityId = e.getEntityId();

        NPC npc = npcManager.getNPCs().get(entityId);
        if (npc == null) {
            return;
        }

        BukkitGamer gamer = GAMER_MANAGER.getGamer(player);
        if (gamer == null) {
            return;
        }

        GamerInteractNPCEvent event = new GamerInteractNPCEvent(gamer, npc,
                (e.getAction() == AsyncPlayerInUseEntityEvent.Action.ATTACK
                        ? GamerInteractNPCEvent.Action.LEFT_CLICK
                        : GamerInteractNPCEvent.Action.RIGHT_CLICK));
        BukkitUtil.runTask(() -> BukkitUtil.callEvent(event));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        npcManager.removeFromTeams(player);
    }
}
