package me.nekocloud.core.api.event.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.core.api.connection.server.Bukkit;
import me.nekocloud.core.api.event.Event;

@RequiredArgsConstructor
@Getter
public class KickedEvent extends Event {

    private final CorePlayer player;
    private final String reason;
    @Setter
    private Bukkit cancelServer;
}
