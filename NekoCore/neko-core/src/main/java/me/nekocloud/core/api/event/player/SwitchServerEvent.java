package me.nekocloud.core.api.event.player;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.core.api.connection.server.Bukkit;
import me.nekocloud.core.api.event.Event;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Getter
public class SwitchServerEvent extends Event {

    CorePlayer corePlayer;
    Bukkit to;
}
