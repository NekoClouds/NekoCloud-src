package me.nekocloud.guilds.core.event.guild;

import lombok.Getter;
import me.nekocloud.guilds.core.event.GuildEvent;
import me.nekocloud.guilds.core.type.Guild;

@Getter
public class GuildChangeNameEvent extends GuildEvent {

    private final String displayName;

    public GuildChangeNameEvent(Guild guild, String displayName) {
        super(guild);
        this.displayName = displayName;

    }
}
