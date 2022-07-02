package me.nekocloud.core.io.info.fields;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Getter
public enum ServerField {

    MIN_PLAYERS("min_players", Integer.class),
    MAX_PLAYERS("max_players", Integer.class),
    MAP_NAME("map_name", String.class),
    ONLINE("online", Integer.class);

    String fieldName;
    Class<?> fieldType;
}
