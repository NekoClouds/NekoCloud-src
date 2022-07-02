package me.nekocloud.guilds.core.event.member;

import me.nekocloud.guilds.core.type.Guild;
import me.nekocloud.guilds.core.type.GuildMember;
import org.jetbrains.annotations.NotNull;

public class GuildMemberQuitEvent extends GuildMemberEvent {

    public GuildMemberQuitEvent(Guild guild, @NotNull GuildMember member) {
        super(guild, member);
    }
}
