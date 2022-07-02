package me.nekocloud.core.api.event.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.nekocloud.base.gamer.constans.Group;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.core.api.event.Event;

@RequiredArgsConstructor
@Getter
public class GroupChangeEvent extends Event {

    private final CorePlayer corePlayer;

    private final Group currentGroup;
    private final Group previousGroup;
}
