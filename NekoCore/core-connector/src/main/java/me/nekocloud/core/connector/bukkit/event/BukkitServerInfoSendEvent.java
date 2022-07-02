package me.nekocloud.core.connector.bukkit.event;

import lombok.*;
import lombok.experimental.FieldDefaults;
import me.nekocloud.core.io.info.ServerInfo;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class BukkitServerInfoSendEvent extends DEvent {
    ServerInfo serverInfo;
}
