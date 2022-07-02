package me.nekocloud.api.event.gamer;

import lombok.Getter;
import lombok.Setter;
import me.nekocloud.api.player.BukkitGamer;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;

@Getter
public class GamerTeleportToGuildHome extends GamerEvent implements Cancellable {

    private final Location location;

    @Setter
    private boolean cancelled;

    public GamerTeleportToGuildHome(BukkitGamer gamer, Location location) {
        super(gamer);
        this.location = location;
    }
}
