package me.nekocloud.core.api.event.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.core.api.event.Event;

@RequiredArgsConstructor
@Getter
public class DisconnectedEvent extends Event {

    private final CorePlayer corePlayer;
}