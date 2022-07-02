package pw.novit.nekocloud.bungee.api.event.player;

import lombok.Getter;
import lombok.Setter;
import me.nekocloud.base.skin.Skin;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import pw.novit.nekocloud.bungee.api.event.PlayerEvent;

@Getter @Setter
public class PlayerChangeSkinEvent extends PlayerEvent {
    Skin setterSkin;

    public PlayerChangeSkinEvent(
            final ProxiedPlayer player,
            final Skin setterSkin
    ) {
        super(player);
        this.setterSkin = setterSkin;
    }
}
