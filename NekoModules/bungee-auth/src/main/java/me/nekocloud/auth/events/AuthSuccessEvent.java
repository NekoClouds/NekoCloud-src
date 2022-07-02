package me.nekocloud.auth.events;

import lombok.*;
import lombok.experimental.FieldDefaults;
import net.md_5.bungee.api.plugin.Event;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Getter
public class AuthSuccessEvent extends Event {
	int playerID;
}
