package me.nekocloud.core.api.event.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import me.nekocloud.core.api.event.Event;
import me.nekocloud.core.common.auth.objects.CoreAuthPlayer;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class AuthSetDiscordEvent extends Event {

    CoreAuthPlayer coreAuthPlayer;
    long discordId;

}
