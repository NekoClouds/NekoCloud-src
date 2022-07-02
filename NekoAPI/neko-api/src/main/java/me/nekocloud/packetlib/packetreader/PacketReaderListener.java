package me.nekocloud.packetlib.packetreader;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import me.nekocloud.packetlib.packetreader.event.AsyncChunkSendEvent;
import me.nekocloud.packetlib.packetreader.event.AsyncPlayerInUseEntityEvent;
import me.nekocloud.base.util.Cooldown;
import me.nekocloud.nekoapi.loader.NekoAPI;
import org.bukkit.entity.Player;

public class PacketReaderListener extends PacketAdapter {

    public PacketReaderListener(NekoAPI nekoAPI) {
        super(nekoAPI, ListenerPriority.NORMAL,
                PacketType.Play.Server.MAP_CHUNK,
                PacketType.Play.Client.USE_ENTITY
        );

        ProtocolLibrary.getProtocolManager().addPacketListener(this);
    }

    @Override
    public void onPacketSending(PacketEvent e) {
        Player player = e.getPlayer();
        PacketContainer packet = e.getPacket();

        if (packet.getType() == PacketType.Play.Server.MAP_CHUNK) {
            /*
            public boolean canSeeChunk(final Player player, final int x, final int z) { //todo мб он нужен
            return ((CraftWorld)player.getWorld()).getHandle().getPlayerChunkMap().a(((CraftPlayer)player).getHandle(), x, z);
            } */
            int x = packet.getIntegers().read(0);
            int z = packet.getIntegers().read(1);

            BukkitUtil.runTaskLater(2L, () -> { // фикс 1.8
                try {
                   String worldName = player.getWorld().getName(); //может быть ошибка, поэтому тут try
                   AsyncChunkSendEvent event = new AsyncChunkSendEvent(player, worldName, x, z);
                  BukkitUtil.runTaskAsync(() -> BukkitUtil.callEvent(event));
                } catch (Exception ignored) {}
            });
        }
    }

    @Override
    public void onPacketReceiving(PacketEvent e) {
        Player player = e.getPlayer();
        PacketContainer packet = e.getPacket();

        if (player == null) {
            return;
        }

        if (packet.getType() == PacketType.Play.Client.USE_ENTITY) {
            if (Cooldown.hasCooldown(player.getName(), "playInUse")) {
                return;
            }

            Cooldown.addCooldown(player.getName(), "playInUse", 5L);

            int entityId = packet.getIntegers().read(0);
            EnumWrappers.EntityUseAction entityUseAction = packet.getEntityUseActions().read(0);
            EnumWrappers.Hand hand = EnumWrappers.Hand.MAIN_HAND;
            if (entityUseAction != EnumWrappers.EntityUseAction.ATTACK) {
                hand = packet.getHands().read(0);
            }

            AsyncPlayerInUseEntityEvent event = new AsyncPlayerInUseEntityEvent(player, entityId,
                    AsyncPlayerInUseEntityEvent.Action.valueOf(entityUseAction.name()),
                    AsyncPlayerInUseEntityEvent.Hand.valueOf(hand.name()));
            BukkitUtil.runTaskAsync(() -> BukkitUtil.callEvent(event));
        }
    }
}
