package me.nekocloud.api.event.gamer;

import lombok.Getter;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.base.gamer.constans.Group;

@Getter
public class GamerChangeGroupEvent extends GamerEvent {

    private final Group group;

    public GamerChangeGroupEvent(BukkitGamer gamer, Group group) {
        super(gamer);
        this.group = group;
    }
}
