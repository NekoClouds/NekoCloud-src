package me.nekocloud.guilds.core.event.member;

import me.nekocloud.guilds.core.type.Guild;
import me.nekocloud.guilds.core.type.GuildMember;
import org.jetbrains.annotations.NotNull;

public class GuildMemberJoinEvent extends GuildMemberEvent {

    public GuildMemberJoinEvent(Guild guild, @NotNull GuildMember member) {
        super(guild, member);
    }
}
