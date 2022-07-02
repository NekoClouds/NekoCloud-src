package pw.novit.nekocloud.bungee.api.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

import static lombok.AccessLevel.PROTECTED;


@Getter
@AllArgsConstructor
@FieldDefaults(level = PROTECTED, makeFinal = true)
public abstract class PlayerEvent extends Event {
    ProxiedPlayer player;
}
