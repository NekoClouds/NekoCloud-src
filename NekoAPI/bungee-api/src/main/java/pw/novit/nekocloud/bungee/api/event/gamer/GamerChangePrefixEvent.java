package pw.novit.nekocloud.bungee.api.event.gamer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import pw.novit.nekocloud.bungee.api.event.GamerEvent;
import pw.novit.nekocloud.bungee.api.gamer.BungeeGamer;

@ToString
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class GamerChangePrefixEvent extends GamerEvent {
    String prefix;

    public GamerChangePrefixEvent(
            final BungeeGamer gamer,
            final String prefix
    ) {
        super(gamer);
        this.prefix = prefix;
    }
}
