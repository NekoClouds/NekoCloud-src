package me.nekocloud.api.event.gamer;

import lombok.Getter;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.base.skin.Skin;

@Getter
public class GamerChangeSkinEvent extends GamerEvent {

    private final Skin skin;

    public GamerChangeSkinEvent(BukkitGamer gamer, Skin skin) {
        super(gamer);
        this.skin = skin;
    }
}
