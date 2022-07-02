package me.nekocloud.packetlib.libraries.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.entity.EntityAPI;
import me.nekocloud.api.entity.npc.NPC;
import me.nekocloud.api.entity.npc.NpcType;
import me.nekocloud.api.entity.npc.types.HumanNPC;
import me.nekocloud.api.entity.stand.CustomStand;
import me.nekocloud.api.exeption.NpcErrorTypeException;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.player.GamerManager;
import me.nekocloud.base.skin.Skin;
import me.nekocloud.nekoapi.loader.NekoAPI;
import me.nekocloud.packetlib.libraries.entity.customstand.CraftStand;
import me.nekocloud.packetlib.libraries.entity.customstand.StandListener;
import me.nekocloud.packetlib.libraries.entity.customstand.StandManager;
import me.nekocloud.packetlib.libraries.entity.npc.NPCListener;
import me.nekocloud.packetlib.libraries.entity.npc.NPCManager;
import me.nekocloud.packetlib.libraries.entity.npc.type.*;
import me.nekocloud.packetlib.libraries.entity.tracker.EntityTrackerListener;
import me.nekocloud.packetlib.libraries.entity.tracker.TrackerEntity;
import me.nekocloud.packetlib.libraries.entity.tracker.TrackerManager;
import me.nekocloud.packetlib.nms.NmsAPI;
import me.nekocloud.packetlib.nms.interfaces.NmsManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EntityAPIImpl implements EntityAPI, TrackerManager {

    GamerManager gamerManager = NekoCloud.getGamerManager();

    NekoAPI nekoAPI;

    StandManager standManager;
    NPCManager npcManager;

    NmsManager nmsManager = NmsAPI.getManager();

    public EntityAPIImpl(NekoAPI nekoAPI) {
        this.nekoAPI = nekoAPI;

        this.standManager = new StandManager();
        this.npcManager = new NPCManager();

        new StandListener(this);
        new NPCListener(this);

        new EntityTrackerListener(nekoAPI, this);
    }

    @Override
    public CustomStand createStand(Location location) {
        return new CraftStand(standManager, location);
    }

    @Override
    public HumanNPC createNPC(Location location, String value, String signature) {
        return new CraftHumanNPC(npcManager, location, value, signature);
    }

    @Override
    public HumanNPC createNPC(Location location, Skin skin) {
        return createNPC(location, skin.getValue(), skin.getSignature());
    }

    @Override
    public HumanNPC createNPC(Location location, Player player) {
        if (player != null && player.isOnline()) {
            BukkitGamer gamer = gamerManager.getGamer(player);
            if (gamer != null)
                return createNPC(location, gamer.getSkin());
        }

        return createNPC(location, "", "");
    }

    @Override
    public <T extends NPC> T createNPC(Location location, NpcType type) {
        NPC npc;
        switch (type) {
            case COW:
                npc = new CraftCowNPC(npcManager, location);
                break;
            case ZOMBIE:
                npc = new CraftZombieNPC(npcManager, location);
                break;
            case VILLAGER:
                npc = new CraftVillagerNPC(npcManager, location);
                break;
            case MUSHROOM_COW:
                npc = new CraftMushroomCowNPC(npcManager, location);
                break;
            case SLIME:
                npc = new CraftSlimeNPC(npcManager, location);
                break;
            case CREEPER:
                npc = new CraftCreeperNPC(npcManager, location);
                break;
            case WOLF:
                npc = new CraftWolfNPC(npcManager, location);
                break;
            case BLAZE:
                npc = new CraftBlazeNPC(npcManager, location);
                break;
            case ENDER_DRAGON:
                npc = new CraftEnderDragonNPC(npcManager, location);
                break;
            case GIANT_ZOMBIE:
                npc = new CraftGiantZombieNPC(npcManager, location);
                break;
            case HUMAN:
            default:
                npc = createNPC(location, "", "");
        }

        if (npc.getType() != type) {
            npcManager.getNPCs().remove(npc.getEntityID());
            throw new NpcErrorTypeException("вы указали неверный Type NPC");
        }

        return (T) npc;
    }

    @Override
    public List<NPC> getNPCs() {
        return npcManager.getNPCs().values()
                .stream()
                .map(craftNPC -> (NPC)craftNPC)
                .collect(Collectors.toList());
    }

    @Override
    public <T extends NPC> List<T> getNPC(NpcType type) {
        return npcManager.getNPCs().values()
                .stream()
                .filter(craftNPC -> craftNPC.getType() == type)
                .map(craftNPC -> (T)craftNPC)
                .collect(Collectors.toList());
    }

    @Override
    public List<CustomStand> getCustomStands() {
        return standManager.getStands().values()
                .stream()
                .map(craftStand -> (CustomStand)craftStand)
                .collect(Collectors.toList());
    }

    @Override
    public List<TrackerEntity> getTrackerEntities() {
        List<TrackerEntity> entities = new ArrayList<>();
        entities.addAll(npcManager.getNPCs().values());
        entities.addAll(standManager.getStands().values());
        return entities;
    }
}
