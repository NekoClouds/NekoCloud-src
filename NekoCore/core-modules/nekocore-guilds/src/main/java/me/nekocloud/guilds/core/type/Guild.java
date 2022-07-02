package me.nekocloud.guilds.core.type;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.chat.ChatColor;
import me.nekocloud.guilds.core.event.guild.GuildChangeNameEvent;
import me.nekocloud.guilds.core.event.member.GuildMemberJoinEvent;
import me.nekocloud.guilds.core.event.member.GuildMemberQuitEvent;

import java.security.Timestamp;
import java.util.Collections;
import java.util.Map;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Guild {

    NekoCore nekoCore;

    String displayName;
    String name;
    String tag;

    final int id;
    final Timestamp date;
    final Map<String, GuildMember> members;

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        this.name = ChatColor.stripColor(this.displayName);

        //guild manager update

        nekoCore.callEvent(new GuildChangeNameEvent(this, this.displayName));
    }

    public void setMotd(String motd) {
        this.tag = tag;

        //guild manager update
    }

    public void addMember(GuildMember member) {
        this.members.put(member.getCorePlayer().getName(), member);

        //guild manager update

        nekoCore.callEvent(new GuildMemberJoinEvent(this, member));
    }

    public GuildMember getMember(String name) {
        return this.members.get(name);
    }

    public void removeMember(String name) {
        GuildMember member = this.members.remove(name);

        //guild manager update

        nekoCore.callEvent(new GuildMemberQuitEvent(this, member));
    }

    public Map<String, GuildMember> getMembers() {
        return Collections.unmodifiableMap(this.members);
    }
}
