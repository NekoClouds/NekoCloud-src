package me.nekocloud.packetlib.libraries.entity.npc;

import io.netty.util.internal.ConcurrentSet;
import lombok.Getter;
import lombok.Setter;
import me.nekocloud.api.entity.EntityEquip;
import me.nekocloud.api.entity.npc.AnimationNpcType;
import me.nekocloud.api.entity.npc.NPC;
import me.nekocloud.api.entity.npc.NpcType;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.util.LocationUtil;
import me.nekocloud.packetlib.libraries.entity.tracker.TrackerEntity;
import me.nekocloud.packetlib.nms.NmsAPI;
import me.nekocloud.packetlib.nms.interfaces.NmsManager;
import me.nekocloud.packetlib.nms.interfaces.entity.DEntityLiving;
import me.nekocloud.packetlib.nms.interfaces.packet.DPacket;
import me.nekocloud.packetlib.nms.interfaces.packet.PacketContainer;
import me.nekocloud.packetlib.nms.interfaces.packet.entity.*;
import me.nekocloud.packetlib.nms.interfaces.packet.scoreboard.PacketScoreBoardTeam;
import me.nekocloud.packetlib.nms.scoreboard.DTeam;
import me.nekocloud.packetlib.nms.scoreboard.TeamAction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public abstract class CraftNPC implements NPC, TrackerEntity {

    protected static final NmsManager NMS_MANAGER = NmsAPI.getManager();
    protected static final PacketContainer PACKET_CONTAINER = NmsAPI.getManager().getPacketContainer();

    protected final NPCManager npcManager;

    @Getter
    protected final String name;

    @Getter
    protected DEntityLiving entity;
    protected Location location;

    private final Set<String> playersSee = new ConcurrentSet<>();
    private final Set<String> headPlayers = new ConcurrentSet<>();

    private final NpcEquip equip;

    @Getter
    @Setter
    private boolean headLook;

    @Getter
    private Player owner;

    private boolean visionAll;
    private boolean glowing;

    protected CraftNPC(NPCManager npcManager, Location location) {
        this.npcManager = npcManager;

        this.name = "§8NPC [" + (int) Math.ceil(Math.random() * 1000) + 2000 + "]";
        this.location = location.clone();
        this.entity = createNMSEntity();

        this.equip = new NpcEquip(this);
        this.headLook = true;

        npcManager.addNPC(this);
    }

    @Override
    public void destroy(Player player) {
        PacketEntityDestroy packet = PACKET_CONTAINER.getEntityDestroyPacket(entity.getEntityID());
        packet.sendPacket(player);
    }

    @Override
    public EntityEquip getEntityEquip() {
        return equip;
    }

    @Override
    public Location getLocation() {
        return location.clone();
    }

    @Override
    public void remove() {
        for (String name : getVisiblePlayers()) {
            destroy(Bukkit.getPlayerExact(name));
        }
        npcManager.removeNPC(this);
    }

    @Override
    public boolean isVisibleTo(Player player) {
        return playersSee.contains(player.getName().toLowerCase());
    }

    @Override
    public Collection<String> getVisiblePlayers() {
        if (!visionAll)
            return playersSee;

        List<String> players = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player -> players.add(player.getName().toLowerCase()));
        return players;
    }

    @Override
    public void hideAll() {
        setPublic(false);
        Bukkit.getOnlinePlayers().forEach(this::removeTo);
    }

    @Override
    public void animation(AnimationNpcType animation) {
        PacketAnimation packet = PACKET_CONTAINER.getAnimationPacket(entity, animation);
        sendNearby(packet);
    }

    @Override
    public boolean isPublic() {
        return visionAll;
    }

    @Override
    public void setPublic(boolean vision) {
        this.visionAll = vision;
    }

    @Override
    public void removeTo(Player player) {
        playersSee.remove(player.getName().toLowerCase());
        destroy(player);
    }

    @Override
    public void removeTo(BukkitGamer gamer) {
        if (gamer == null)
            return;

        Player player = gamer.getPlayer();
        if (player == null || !player.isOnline())
            return;

        removeTo(player);
    }

    @Override
    public void showTo(BukkitGamer gamer) {
        if (gamer == null)
            return;

        Player player = gamer.getPlayer();
        if (player == null || !player.isOnline())
            return;

        showTo(player);
    }

    @Override
    public void showTo(Player player) {
        if (player == null || !player.isOnline())
            return;

        String name = player.getName().toLowerCase();
        if (playersSee.contains(name))
            return;

        spawn(player);
    }

    @Override
    public int getEntityID() {
        if (entity == null)
            return -1;
        return entity.getEntityID();
    }

    @Override
    public void setLook(float yaw, float pitch) {
        location.setYaw(yaw);
        location.setPitch(pitch);

        entity.setLocation(location);

        PacketEntityLook lookPacket = PACKET_CONTAINER
                .getEntityLookPacket(entity, LocationUtil.getFixRotation(yaw), LocationUtil.getFixRotation(pitch));

        PacketEntityHeadRotation packetHead = PACKET_CONTAINER
                .getEntityHeadRotationPacket(entity, LocationUtil.getFixRotation(yaw));
        sendNearby(lookPacket);
        sendNearby(packetHead);
    }

    @Override
    public Set<String> getHeadPlayers() {
        return headPlayers;
    }

    @Override
    public void onTeleport(Location location) {
        this.location = location;

        entity.setLocation(location);

        sendNearby(PACKET_CONTAINER.getEntityTeleportPacket(entity));
    }

    @Override
    public void setOwner(Player owner) {
        if (this.owner == owner)
            return;

        this.owner = owner;
        showTo(owner);
    }

    @Override
    public boolean canSee(Player player) {
        if (player == null) {
            return false;
        }
        return (visionAll || playersSee.contains(player.getName().toLowerCase())) && location.getWorld() == player.getLocation().getWorld();
    }

    @Override
    public boolean isGlowing() {
        return glowing;
    }

    @Override
    public void setGlowing(boolean glowing) {
        if (this.glowing == glowing) {
            return;
        }

        this.glowing = glowing;
        entity.setGlowing(glowing);
        PacketEntityMetadata packet = PACKET_CONTAINER.getEntityMetadataPacket(entity);
        sendNearby(packet);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" + location + "}";
    }

    public final void spawn(Player player) {
        if (player.getWorld() != location.getWorld()) {
            return;
        }
        spawnEntity(player);
        addPlayer(player);
    }

    private void addPlayer(Player player) {
        if (entity == null) {
            return;
        }

        PacketEntityMetadata watchPacket = PACKET_CONTAINER.getEntityMetadataPacket(entity);
        watchPacket.sendPacket(player);

        sendHeadRotation(player, location.getYaw(), location.getPitch()); //поворот головы

        PacketAnimation animation = PACKET_CONTAINER.getAnimationPacket(entity, AnimationNpcType.SWING_MAIN_HAND);
        //чтобы тело было повернуто как надо
        animation.sendPacket(player);

        equip.getItemsEquip().forEach((equipType, itemStack) -> {
            PacketEntityEquipment equipPacket = PACKET_CONTAINER.getEntityEquipmentPacket(entity, equipType, itemStack);
            equipPacket.sendPacket(player);
        });

        if (getType() == NpcType.HUMAN)
            addNPCToTeam(player, this);

        String name = player.getName().toLowerCase();
        playersSee.add(name);
    }

    @Override
    public void sendHeadRotation(Player player, float yaw, float pitch) {
        PacketEntityLook packetLook = PACKET_CONTAINER.getEntityLookPacket(entity,
                LocationUtil.getFixRotation(yaw), LocationUtil.getFixRotation(pitch));

        PacketEntityHeadRotation packetHead = PACKET_CONTAINER.getEntityHeadRotationPacket(entity,
                LocationUtil.getFixRotation(yaw));

        PACKET_CONTAINER.sendPacket(player, packetLook, packetHead);
    }

    protected void sendNearby(DPacket packet) {
        for (String name : playersSee) {
            Player player = Bukkit.getPlayer(name);
            if (player == null || player.getLocation().getWorld() != location.getWorld())
                continue;

            packet.sendPacket(player);
        }
    }

    //добавляем НПС в тиму и тогда у него не появляется всякое говно
    private void addNPCToTeam(Player player, NPC npc) {
        DTeam team = new DTeam(npc.getName() + "_TEAM");
        team.addPlayer(npc.getName());
        PacketScoreBoardTeam packet = PACKET_CONTAINER.getScoreBoardTeamPacket(team, TeamAction.PLAYERS_ADD);
        packet.sendPacket(player);
    }

    abstract public DEntityLiving createNMSEntity();

    abstract public void spawnEntity(Player player);
}
