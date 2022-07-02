package me.nekocloud.packetlib.nms.interfaces.packet;

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
import me.nekocloud.api.effect.ParticleEffect;
import me.nekocloud.api.entity.EquipType;
import me.nekocloud.api.entity.npc.AnimationNpcType;
import me.nekocloud.api.scoreboard.DisplaySlot;
import me.nekocloud.packetlib.nms.interfaces.DWorldBorder;
import me.nekocloud.packetlib.nms.interfaces.entity.DEntity;
import me.nekocloud.packetlib.nms.interfaces.entity.DEntityLiving;
import me.nekocloud.packetlib.nms.interfaces.entity.DEntityPlayer;
import me.nekocloud.packetlib.nms.scoreboard.*;
import me.nekocloud.packetlib.nms.types.*;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface PacketContainer {

    void sendPacket(Player player, DPacket... dPackets);

    void sendChatPacket(Player player, String message, ChatMessageType messageType);

    void sendTitlePacket(Player player, TitleActionType type, String message);
    void sendTitlePacket(Player player, int fadeIn, int stay, int fadeOut);

    void sendWorldBorderPacket(Player player, DWorldBorder border, WorldBorderActionType type);

    PacketScoreBoardTeam getScoreBoardTeamPacket(DTeam team, TeamAction action);

    PacketDisplayObjective getDisplayObjectivePacket(DisplaySlot slot, DObjective objective);

    PacketScoreboardObjective getScoreboardObjectivePacket(DObjective objective, ObjectiveActionMode mode);

    PacketScoreboardScore getScoreboardScorePacket(DScore score, ScoreboardAction action);

    PacketNamedEntitySpawn getNamedEntitySpawnPacket(DEntityPlayer entityPlayer);

    PacketPlayerInfo getPlayerInfoPacket(DEntityPlayer entityPlayer, PlayerInfoActionType actionType);

    PacketAnimation getAnimationPacket(DEntity entity, AnimationNpcType animation);

    PacketAttachEntity getAttachEntityPacket(DEntity entity, DEntity vehicle);

    PacketEntityDestroy getEntityDestroyPacket(int... entityIDs);

    PacketMount getMountPacket(DEntity entity);

    PacketEntityMetadata getEntityMetadataPacket(DEntity entity);

    PacketCamera getCameraPacket(Player player);

    PacketEntityLook getEntityLookPacket(DEntity entity, byte yaw, byte pitch);

    PacketEntityEquipment getEntityEquipmentPacket(DEntity entity, EquipType slot, ItemStack itemStack);

    PacketEntityHeadRotation getEntityHeadRotationPacket(DEntity entity, byte yaw);

    PacketSpawnEntity getSpawnEntityPacket(DEntity entity, EntitySpawnType entitySpawnType, int objectData);

    PacketSpawnEntityLiving getSpawnEntityLivingPacket(DEntityLiving entityLiving);

    PacketEntityTeleport getEntityTeleportPacket(DEntity entity);

    PacketBed getBedPacket(DEntityPlayer entity, Location bed);

    PacketWorldParticles getWorldParticlesPacket(ParticleEffect effect, boolean longDistance, Location center,
                                                 float offsetX, float offsetY, float offsetZ, float speed,
                                                 int amount, int... data);

}
