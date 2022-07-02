package me.nekocloud.api.event.gamer;

import lombok.Getter;
import me.nekocloud.api.player.BukkitGamer;

@Getter
public class GamerChangePrefixEvent extends GamerEvent {

    private final String prefix;

    public GamerChangePrefixEvent(BukkitGamer gamer, String prefix) {
        super(gamer);
        this.prefix = prefix;
    }
}

