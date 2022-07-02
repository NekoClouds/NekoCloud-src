package me.nekocloud.api.event.gamer;

import lombok.Getter;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.api.entity.stand.CustomStand;

@Getter
public class GamerInteractCustomStandEvent extends GamerEvent {

    private final CustomStand stand;
    private final Action action;

    public GamerInteractCustomStandEvent(BukkitGamer gamer, CustomStand stand, Action action) {
        super(gamer);
        this.stand = stand;
        this.action = action;
    }

    public enum Action {
        LEFT_CLICK, RIGHT_CLICK
    }
}
