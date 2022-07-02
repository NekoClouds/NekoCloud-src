package me.nekocloud.guilds.core.event.member;

import lombok.Getter;
import me.nekocloud.guilds.core.event.GuildEvent;
import me.nekocloud.guilds.core.type.Guild;
import me.nekocloud.guilds.core.type.GuildMember;
import org.jetbrains.annotations.NotNull;

@Getter
public class GuildMemberEvent extends GuildEvent {

    private final GuildMember member;

    public GuildMemberEvent(Guild guild, @NotNull GuildMember member) {
        super(guild);
        this.member = member;
    }
}
