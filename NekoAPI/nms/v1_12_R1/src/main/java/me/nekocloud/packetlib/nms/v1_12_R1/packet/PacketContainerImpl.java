package me.nekocloud.packetlib.nms.v1_12_R1.packet;

import me.nekocloud.api.effect.ParticleEffect;
import me.nekocloud.api.entity.EquipType;
import me.nekocloud.api.entity.npc.AnimationNpcType;
import me.nekocloud.api.scoreboard.DisplaySlot;
import me.nekocloud.packetlib.nms.interfaces.DWorldBorder;
import me.nekocloud.packetlib.nms.interfaces.entity.DEntity;
import me.nekocloud.packetlib.nms.interfaces.entity.DEntityLiving;
import me.nekocloud.packetlib.nms.interfaces.entity.DEntityPlayer;
import me.nekocloud.packetlib.nms.interfaces.packet.DPacket;
import me.nekocloud.packetlib.nms.interfaces.packet.PacketContainer;
import me.nekocloud.packetlib.nms.interfaces.packet.entity.*;
import me.nekocloud.packetlib.nms.interfaces.packet.entityplayer.PacketBed;
import me.nekocloud.packetlib.nms.interfaces.packet.entityplayer.PacketCamera;
import me.nekocloud.packetlib.nms.interfaces.packet.entityplayer.PacketNamedEntitySpawn;
import me.nekocloud.packetlib.nms.interfaces.packet.entityplayer.PacketPlayerInfo;
import me.nekocloud.packetlib.nms.interfaces.packet.scoreboard.PacketDisplayObjective;
import me.nekocloud.packetlib.nms.interfaces.packet.scoreboard.PacketScoreBoardTeam;
import me.nekocloud.packetlib.nms.interfaces.packet.scoreboard.PacketScoreboardObjective;
import me.nekocloud.packetlib.nms.interfaces.packet.scoreboard.PacketScoreboardScore;
import me.nekocloud.packetlib.nms.interfaces.packet.world.PacketWorldParticles;
import me.nekocloud.packetlib.nms.scoreboard.*;
import me.nekocloud.packetlib.nms.types.*;
import me.nekocloud.packetlib.nms.types.ChatMessageType;
import me.nekocloud.packetlib.nms.v1_12_R1.DWorldBorderImpl;
import me.nekocloud.packetlib.nms.v1_12_R1.packet.entity.*;
import me.nekocloud.packetlib.nms.v1_12_R1.packet.entityplayer.PacketBedImpl;
import me.nekocloud.packetlib.nms.v1_12_R1.packet.entityplayer.PacketCameraImpl;
import me.nekocloud.packetlib.nms.v1_12_R1.packet.entityplayer.PacketNamedEntitySpawnImpl;
import me.nekocloud.packetlib.nms.v1_12_R1.packet.entityplayer.PacketPlayerInfoImpl;
import me.nekocloud.packetlib.nms.v1_12_R1.packet.scoreboard.PacketDisplayObjectiveImpl;
import me.nekocloud.packetlib.nms.v1_12_R1.packet.scoreboard.PacketScoreBoardTeamImpl;
import me.nekocloud.packetlib.nms.v1_12_R1.packet.scoreboard.PacketScoreboardObjectiveImpl;
import me.nekocloud.packetlib.nms.v1_12_R1.packet.scoreboard.PacketScoreboardScoreImpl;
import me.nekocloud.packetlib.nms.v1_12_R1.packet.world.PacketWorldParticlesImpl;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class PacketContainerImpl implements PacketContainer {

    @Override
    public void sendPacket(final Player player, DPacket... dPackets) {
        if (dPackets.length == 0) {
            return;
        }

        Arrays.asList(dPackets).forEach(packet -> packet.sendPacket(player));
    }

    @Override
    public void sendChatPacket(Player player, String message,
                               ChatMessageType messageType) {
        PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer
                .a(messageType == ChatMessageType.GAME_INFO
                        ? "{\"text\": \"" + message + "\"}" : message), net.minecraft.server.v1_12_R1.ChatMessageType.valueOf(messageType.name()));
        sendPacket(player, packetPlayOutChat);
    }

    @Override
    public void sendTitlePacket(Player player, TitleActionType type, String message) {
        PacketPlayOutTitle packet = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.valueOf(type.name()),
                IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}"));
        sendPacket(player, packet);
    }

    @Override
    public void sendTitlePacket(Player player, int fadeIn, int stay, int fadeOut) {
        PacketPlayOutTitle packet = new PacketPlayOutTitle(fadeIn, stay, fadeOut);
        sendPacket(player, packet);
    }

    @Override
    public void sendWorldBorderPacket(Player player, DWorldBorder border, WorldBorderActionType type) {
        WorldBorder worldBorder = ((DWorldBorderImpl)border).get();
        PacketPlayOutWorldBorder packet = new PacketPlayOutWorldBorder(worldBorder,
                PacketPlayOutWorldBorder.EnumWorldBorderAction.valueOf(type.name()));
        sendPacket(player, packet);
    }

    @Override
    public PacketScoreBoardTeam getScoreBoardTeamPacket(DTeam team, TeamAction action) {
        return new PacketScoreBoardTeamImpl(team, action);
    }

    @Override
    public PacketDisplayObjective getDisplayObjectivePacket(DisplaySlot slot, DObjective objective) {
        return new PacketDisplayObjectiveImpl(slot, objective);
    }

    @Override
    public PacketScoreboardObjective getScoreboardObjectivePacket(DObjective objective, ObjectiveActionMode mode) {
        return new PacketScoreboardObjectiveImpl(objective, mode);
    }

    @Override
    public PacketScoreboardScore getScoreboardScorePacket(DScore score, ScoreboardAction action) {
        return new PacketScoreboardScoreImpl(score, action);
    }

    @Override
    public PacketNamedEntitySpawn getNamedEntitySpawnPacket(DEntityPlayer entityPlayer) {
        return new PacketNamedEntitySpawnImpl(entityPlayer);
    }

    @Override
    public PacketPlayerInfo getPlayerInfoPacket(DEntityPlayer entityPlayer, PlayerInfoActionType actionType) {
        return new PacketPlayerInfoImpl(entityPlayer, actionType);
    }

    private void sendPacket(Player player, Packet<?> packet) {
        if (player == null || !player.isOnline())
            return;

        EntityPlayer handle = ((CraftPlayer) player).getHandle();
        if (handle == null)
            return;

        PlayerConnection playerConnection = handle.playerConnection;
        if (playerConnection == null)
            return;

        playerConnection.sendPacket(packet);
    }

    @Override
    public PacketAnimation getAnimationPacket(DEntity entity, AnimationNpcType animation) {
        return new PacketAnimationImpl(entity, animation);
    }

    @Override
    public PacketAttachEntity getAttachEntityPacket(DEntity entity, DEntity vehicle) {
        return new PacketAttachEntityImpl(entity, vehicle);
    }

    @Override
    public PacketEntityDestroy getEntityDestroyPacket(int... entityIDs) {
        return new PacketEntityDestroyImpl(entityIDs);
    }

    @Override
    public PacketMount getMountPacket(DEntity entity) {
        return new PacketMountImpl(entity);
    }

    @Override
    public PacketEntityMetadata getEntityMetadataPacket(DEntity entity) {
        return new PacketEntityMetadataImpl(entity);
    }

    @Override
    public PacketCamera getCameraPacket(Player player) {
        return new PacketCameraImpl(player);
    }

    @Override
    public PacketEntityLook getEntityLookPacket(DEntity entity, byte yaw, byte pitch) {
        return new PacketEntityLookImpl(entity, yaw, pitch);
    }

    @Override
    public PacketEntityEquipment getEntityEquipmentPacket(DEntity entity, EquipType slot, ItemStack itemStack) {
        return new PacketEntityEquipmentImpl(entity, slot, itemStack);
    }

    @Override
    public PacketEntityHeadRotation getEntityHeadRotationPacket(DEntity entity, byte yaw) {
        return new PacketEntityHeadRotationImpl(entity, yaw);
    }

    @Override
    public PacketSpawnEntity getSpawnEntityPacket(DEntity entity, EntitySpawnType entitySpawnType, int objectData) {
        return new PacketSpawnEntityImpl(entity, entitySpawnType, objectData);
    }

    @Override
    public PacketSpawnEntityLiving getSpawnEntityLivingPacket(DEntityLiving entityLiving) {
        return new PacketSpawnEntityLivingImpl(entityLiving);
    }

    @Override
    public PacketEntityTeleport getEntityTeleportPacket(DEntity entity) {
        return new PacketEntityTeleportImpl(entity);
    }

    @Override
    public PacketBed getBedPacket(DEntityPlayer entity, Location bed) {
        return new PacketBedImpl(entity, bed);
    }

    @Override
    public PacketWorldParticles getWorldParticlesPacket(ParticleEffect effect, boolean longDistance, Location center,
                                                        float offsetX, float offsetY, float offsetZ, float speed,
                                                        int amount, int... data) {
        return new PacketWorldParticlesImpl(effect, longDistance, center, offsetX,
                offsetY, offsetZ, speed, amount, data);
    }


}
