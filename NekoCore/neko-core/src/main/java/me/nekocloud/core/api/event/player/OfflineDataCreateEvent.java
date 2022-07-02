package me.nekocloud.core.api.event.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.core.api.event.Event;

@Getter
@AllArgsConstructor
public class OfflineDataCreateEvent extends Event {

    private final CorePlayer player;
}
