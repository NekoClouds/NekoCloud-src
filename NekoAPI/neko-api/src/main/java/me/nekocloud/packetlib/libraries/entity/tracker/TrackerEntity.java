package me.nekocloud.packetlib.libraries.entity.tracker;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Set;

public interface TrackerEntity {

    int getEntityID();

    Location getLocation();
    @Nullable
    Player getOwner();

    void spawn(Player player);
    void destroy(Player player);

    void removeTo(Player player);

    boolean canSee(Player player);
    boolean isHeadLook(); //можно ли вертеть головой

    void remove();

    void sendHeadRotation(Player player, float yaw, float pitch);

    Set<String> getHeadPlayers();
}
