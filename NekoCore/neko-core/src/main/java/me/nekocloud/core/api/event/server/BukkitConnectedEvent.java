package me.nekocloud.core.api.event.server;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import me.nekocloud.core.api.connection.server.Bukkit;
import me.nekocloud.core.api.event.Event;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Getter
public class BukkitConnectedEvent extends Event {

    Bukkit bukkit;
}
