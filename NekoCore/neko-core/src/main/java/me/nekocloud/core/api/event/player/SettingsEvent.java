package me.nekocloud.core.api.event.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import me.nekocloud.base.gamer.constans.SettingsType;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.core.api.event.Event;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SettingsEvent extends Event {

    CorePlayer corePlayer;
    SettingsType settingsType;
    boolean key;

}
