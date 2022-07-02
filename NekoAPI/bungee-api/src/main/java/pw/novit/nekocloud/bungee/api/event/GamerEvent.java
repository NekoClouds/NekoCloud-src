package pw.novit.nekocloud.bungee.api.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.md_5.bungee.api.plugin.Event;
import pw.novit.nekocloud.bungee.api.gamer.BungeeGamer;

import static lombok.AccessLevel.PROTECTED;


@Getter
@AllArgsConstructor
@FieldDefaults(level = PROTECTED, makeFinal = true)
public abstract class GamerEvent extends Event {
    BungeeGamer gamer;
}
