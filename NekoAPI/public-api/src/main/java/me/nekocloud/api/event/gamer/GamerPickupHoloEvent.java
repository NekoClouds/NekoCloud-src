package me.nekocloud.api.event.gamer;

import lombok.Getter;
import lombok.Setter;
import me.nekocloud.api.hologram.Hologram;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.hologram.lines.ItemDropLine;

@Getter
public class GamerPickupHoloEvent extends GamerEvent {

    private final Hologram hologram;
    private final ItemDropLine line;

    @Setter
    private boolean remove;

    public GamerPickupHoloEvent(BukkitGamer gamer, Hologram hologram, ItemDropLine itemDropLine){
        super(gamer);
        this.hologram = hologram;
        this.line = itemDropLine;
    }
}
