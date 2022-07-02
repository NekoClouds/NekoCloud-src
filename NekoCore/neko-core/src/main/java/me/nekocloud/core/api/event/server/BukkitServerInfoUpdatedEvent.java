package me.nekocloud.core.api.event.server;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import me.nekocloud.core.api.connection.server.Bukkit;
import me.nekocloud.core.api.event.Event;
import me.nekocloud.core.io.info.ServerInfo;
import org.jetbrains.annotations.NotNull;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Getter @Setter
public class BukkitServerInfoUpdatedEvent extends Event {

    Bukkit bukkit;
    @NotNull @NonFinal
    ServerInfo serverInfo;

    boolean versionChanged;
}
