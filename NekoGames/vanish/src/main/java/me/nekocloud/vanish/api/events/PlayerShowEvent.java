package me.nekocloud.vanish.api.events;

import lombok.Setter;
import me.nekocloud.api.event.player.PlayerEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

@Setter
public class PlayerShowEvent extends PlayerEvent implements Cancellable {

    private boolean isCancelled = false;

    protected PlayerShowEvent(Player player) {
        super(player);
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }
}
