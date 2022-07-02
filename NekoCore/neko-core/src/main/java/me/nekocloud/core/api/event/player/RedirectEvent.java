package me.nekocloud.core.api.event.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.core.api.connection.server.Bukkit;
import me.nekocloud.core.api.event.Cancellable;
import me.nekocloud.core.api.event.Event;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RedirectEvent extends Event implements Cancellable {

    @NonFinal boolean cancelled;

    CorePlayer corePlayer;
    Bukkit from;
    Bukkit to;

    public RedirectEvent(CorePlayer corePlayer, Bukkit from, @NonNull Bukkit to) {
        this.corePlayer = corePlayer;
        this.from = from;
        this.to = to;
    }
}
