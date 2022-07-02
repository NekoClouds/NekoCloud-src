package me.nekocloud.core.connector.bukkit.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import me.nekocloud.core.io.info.ServerInfo;
import me.nekocloud.core.io.info.filter.ServerFilter;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Getter
@AllArgsConstructor
public class BukkitFilterResponseEvent extends DEvent {
    String regex;
    ServerFilter filter;
    List<ServerInfo> serversInfos;
}
