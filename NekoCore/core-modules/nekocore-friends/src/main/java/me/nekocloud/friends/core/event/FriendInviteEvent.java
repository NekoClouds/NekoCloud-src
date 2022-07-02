package me.nekocloud.friends.core.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import me.nekocloud.core.api.event.Cancellable;
import me.nekocloud.core.api.event.Event;
import me.nekocloud.core.api.connection.player.CorePlayer;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Getter
public class FriendInviteEvent extends Event implements Cancellable {

    final CorePlayer player;
    final CorePlayer target;

    @Setter @NonFinal
    boolean cancelled;
}
