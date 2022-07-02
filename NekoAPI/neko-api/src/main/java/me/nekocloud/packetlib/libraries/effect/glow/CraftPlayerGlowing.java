package me.nekocloud.packetlib.libraries.effect.glow;

import lombok.Getter;
import me.nekocloud.api.effect.PlayerGlowing;
import me.nekocloud.packetlib.nms.NmsAPI;
import me.nekocloud.packetlib.nms.interfaces.NmsManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public class CraftPlayerGlowing implements PlayerGlowing {

    private final NmsManager nmsManager = NmsAPI.getManager();

    private final GlowManager glowManager;
    private final Player owner;
    private final Set<String> glowingsEntity = Collections.synchronizedSet(new HashSet<>());

    public CraftPlayerGlowing(GlowManager glowManager, Player owner, List<Player> canSee) {
        this.glowManager = glowManager;
        this.owner = owner;

        for (Player other : canSee) {
            if (other == owner) {
                continue;
            }

            glowingsEntity.add(other.getName().toLowerCase());
            nmsManager.sendGlowing(owner, other, true);
        }
    }

    @Override
    public void addEntity(Player player) {
        if (player == null || player.getName().equalsIgnoreCase(owner.getName())) {
            return;
        }

        String name = player.getName().toLowerCase();
        if (glowingsEntity.contains(name)) {
            return;
        }

        glowingsEntity.add(name);
        nmsManager.sendGlowing(getOwner(), player, true);
    }

    @Override
    public void addEntity(Player... players) {
        addEntity(Arrays.asList(players));
    }

    @Override
    public void addEntity(List<Player> players) {
        for (Player other : players) {
            String name = other.getName().toLowerCase();
            if (name.equalsIgnoreCase(owner.getName()) || glowingsEntity.contains(name)) {
                continue;
            }

            glowingsEntity.add(name);
            nmsManager.sendGlowing(getOwner(), other, true);
        }
    }

    @Override
    public void removeEntity(Player other) {
        if (other == null || !other.isOnline()) {
            return;
        }

        String name = other.getName().toLowerCase();
        if (name.equalsIgnoreCase(owner.getName()) || !glowingsEntity.remove(name)) {
            return;
        }

        nmsManager.sendMetaData(getOwner(), other);
    }

    @Override
    public void removeEntity(Player... players) {
        removeEntity(Arrays.asList(players));
    }

    @Override
    public void removeEntity(List<Player> players) {
        for (Player other : players) {
            String name = other.getName().toLowerCase();
            if (owner.getName().equalsIgnoreCase(name) || !glowingsEntity.remove(name) || !other.isOnline()) {
                continue;
            }

            nmsManager.sendMetaData(getOwner(), other);
        }
    }

    @Override
    public List<String> getPlayers() {
        return new ArrayList<>(glowingsEntity);
    }

    @Override
    public void remove() {
        if (owner == null) {
            return;
        }

        for (String name : glowingsEntity) {
            Player other = Bukkit.getPlayer(name);
            if (other == null || !other.isOnline()) {
                continue;
            }

            nmsManager.sendMetaData(owner, other);
        }
        glowManager.getGlowings().remove(owner.getName().toLowerCase());
    }
}
