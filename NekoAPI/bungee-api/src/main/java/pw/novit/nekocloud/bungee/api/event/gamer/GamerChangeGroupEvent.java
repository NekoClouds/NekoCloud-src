package pw.novit.nekocloud.bungee.api.event.gamer;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import me.nekocloud.base.gamer.constans.Group;
import pw.novit.nekocloud.bungee.api.event.GamerEvent;
import pw.novit.nekocloud.bungee.api.gamer.BungeeGamer;

import static lombok.AccessLevel.PRIVATE;

@FieldDefaults(level = PRIVATE, makeFinal = true)
@Getter
@ToString
public final class GamerChangeGroupEvent extends GamerEvent {
    Group group;

    public GamerChangeGroupEvent(
            final BungeeGamer gamer,
            final Group group
    ) {
        super(gamer);
        this.group = group;
    }
}
