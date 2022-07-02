package me.nekocloud.punishment.core.api.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.core.api.event.Event;
import me.nekocloud.punishment.PunishmentType;

import static lombok.AccessLevel.PRIVATE;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class PunishmentGiveEvent extends Event {
	CorePlayer owner, intruder;
	PunishmentType type;
	String reason;
}
