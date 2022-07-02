package me.nekocloud.core.connector.bukkit.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Getter
@AllArgsConstructor
public class BukkitOnlineFetchEvent extends DEvent {

    String regex;
    int online;
}
