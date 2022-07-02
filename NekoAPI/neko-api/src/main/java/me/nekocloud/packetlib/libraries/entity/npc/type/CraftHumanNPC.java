package me.nekocloud.packetlib.libraries.entity.npc.type;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.nekocloud.api.entity.npc.AnimationNpcType;
import me.nekocloud.api.entity.npc.NpcType;
import me.nekocloud.api.entity.npc.types.HumanNPC;
import me.nekocloud.base.skin.Skin;
import me.nekocloud.base.skin.SkinType;
import me.nekocloud.nekoapi.utils.bukkit.BukkitUtil;
import me.nekocloud.packetlib.libraries.entity.npc.CraftNPC;
import me.nekocloud.packetlib.libraries.entity.npc.NPCManager;
import me.nekocloud.packetlib.nms.interfaces.entity.DEntityPlayer;
import me.nekocloud.packetlib.nms.interfaces.packet.entity.PacketEntityTeleport;
import me.nekocloud.packetlib.nms.interfaces.packet.entityplayer.PacketBed;
import me.nekocloud.packetlib.nms.interfaces.packet.entityplayer.PacketNamedEntitySpawn;
import me.nekocloud.packetlib.nms.interfaces.packet.entityplayer.PacketPlayerInfo;
import me.nekocloud.packetlib.nms.interfaces.packet.scoreboard.PacketScoreBoardTeam;
import me.nekocloud.packetlib.nms.scoreboard.DTeam;
import me.nekocloud.packetlib.nms.scoreboard.TagVisibility;
import me.nekocloud.packetlib.nms.scoreboard.TeamAction;
import me.nekocloud.packetlib.nms.types.PlayerInfoActionType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CraftHumanNPC extends CraftNPC implements HumanNPC {

    private boolean bed = false;
    private Skin skin;

    private ChatColor color;

    public CraftHumanNPC(NPCManager npcManager, Location location, String value, String signature) {
        super(npcManager, location);

        this.color = ChatColor.GOLD;

        //костыль, тк createNMSEntity сработает раньше, поэтому нужно 2й раз делать так
        createEntity(value, signature);

        npcManager.addNPC(this);
    }

    private void createEntity(String value, String signature) {
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), name);
        gameProfile.getProperties().put("textures", new Property("textures", value, signature));
        this.skin = new Skin(this.name, value, signature, SkinType.CUSTOM);

        this.entity = NMS_MANAGER.createEntityPlayer(location.getWorld(), gameProfile);

        entity.setLocation(location);
        entity.setCustomNameVisible(false);
        entity.setCustomName(name);

        entity.watch(13, (byte) 127);
    }

    @Override
    public void changeSkin(String value, String signature) {
        for (String name : getVisiblePlayers()) {
            destroy(Bukkit.getPlayerExact(name));
        }

        createEntity(value, signature);
        npcManager.addNPC(this);

        BukkitUtil.runTaskLaterAsync(5L, ()-> {
            for (String name : getVisiblePlayers()) {
                Player player = Bukkit.getPlayer(name);
                if (player == null || !player.isOnline()) {
                    continue;
                }
                spawn(player);
            }
        });
    }

    @Override
    public void changeSkin(Skin skin) {
        changeSkin(skin.getValue(), skin.getSignature());
    }

    @Override
    public void setBed(boolean bed) {
        if (entity == null)
            return;

        if (this.bed == bed)
            return;

        this.bed = bed;

        if (bed) {
            Location bedLocation = this.getLocation();//new Location(getLocation().getWorld(), 1, 1,1);
            bedLocation.setY(1);
            PacketBed packetBed = PACKET_CONTAINER.getBedPacket(getEntity(), bedLocation);
            for (String name : getVisiblePlayers()) {
                Player player = Bukkit.getPlayer(name);
                if (player == null) {
                    continue;
                }

                player.sendBlockChange(bedLocation, Material.BED_BLOCK, (byte)0);
                packetBed.sendPacket(player);
            }
            return;
        }

        animation(AnimationNpcType.LEAVE_BED);
        PacketEntityTeleport teleportPacket = PACKET_CONTAINER.getEntityTeleportPacket(entity);
        sendNearby(teleportPacket);
    }

    @Override
    public boolean isLeavedBed() {
        return !bed;
    }

    @Override
    public Skin getSkin() {
        if (skin == null) {
            return new Skin("test", "", "", SkinType.CUSTOM);
        }

        return skin;
    }

    public DEntityPlayer getEntity() {
        return (DEntityPlayer) entity;
    }

    @Override
    public DEntityPlayer createNMSEntity() {
        return null; //nothingч
    }

    @Override
    public void spawnEntity(Player player) {
        if (entity == null) {
            return;
        }

        //создаем игроку тиму в которую будут добавлены все НПС, чтобы он их не видел(ники НПС) и передаем ее игроку,
        npcManager.addPlayerToTeam(player, this, color);

        PacketPlayerInfo addPacket = PACKET_CONTAINER.getPlayerInfoPacket((DEntityPlayer) entity,
                PlayerInfoActionType.ADD_PLAYER);
        addPacket.sendPacket(player);

        PacketNamedEntitySpawn spawnPacket = PACKET_CONTAINER.getNamedEntitySpawnPacket((DEntityPlayer) entity);
        spawnPacket.sendPacket(player);

        if (bed) {
            Location bedLocation = this.getLocation();//new Location(getLocation().getWorld(), 1, 1,1);
            bedLocation.setY(1);
            PacketBed packetBed = PACKET_CONTAINER.getBedPacket(getEntity(), bedLocation);
            player.sendBlockChange(bedLocation, Material.BED_BLOCK, (byte)0);
            packetBed.sendPacket(player);
        }

        BukkitUtil.runTaskLaterAsync(100L, ()-> {
            PacketPlayerInfo removePacket = PACKET_CONTAINER.getPlayerInfoPacket(getEntity(),
                    PlayerInfoActionType.REMOVE_PLAYER);
            removePacket.sendPacket(player);
        });
    }


    @Override
    public void setGlowing(ChatColor color) {
        if (color == null)
            return;

        if (this.color == color)
            return;

        this.color = color;

        DTeam team = new DTeam(this.name + "_TEAM");
        team.setPrefix(color.toString());
        team.setVisibility(TagVisibility.NEVER);
        PacketScoreBoardTeam packet = PACKET_CONTAINER.getScoreBoardTeamPacket(team, TeamAction.UPDATE);
        sendNearby(packet);

        setGlowing(true);
    }

    @Override
    public NpcType getType() {
        return NpcType.HUMAN;
    }
}
