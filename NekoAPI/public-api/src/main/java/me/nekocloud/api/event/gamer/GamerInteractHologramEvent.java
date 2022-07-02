package me.nekocloud.api.event.gamer;

import lombok.Getter;
import me.nekocloud.api.hologram.Hologram;
import me.nekocloud.api.player.BukkitGamer;

@Getter
public class GamerInteractHologramEvent extends GamerEvent {

    private final Hologram hologram;
    private final GamerInteractCustomStandEvent.Action action;

    public GamerInteractHologramEvent(BukkitGamer gamer, Hologram hologram,
                                      GamerInteractCustomStandEvent.Action action) {
        super(gamer);
        this.hologram = hologram;
        this.action = action;
    }
}
