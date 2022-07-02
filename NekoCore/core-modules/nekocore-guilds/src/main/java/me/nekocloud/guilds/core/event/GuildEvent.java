package me.nekocloud.guilds.core.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.nekocloud.core.api.event.Event;
import me.nekocloud.guilds.core.type.Guild;

@Getter
@AllArgsConstructor
public abstract class GuildEvent extends Event {

    protected final Guild guild;

}
