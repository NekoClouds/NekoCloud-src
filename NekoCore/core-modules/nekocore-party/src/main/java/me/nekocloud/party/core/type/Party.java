package me.nekocloud.party.core.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.connection.player.CorePlayer;
import me.nekocloud.core.api.connection.server.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class Party {

    private final String leaderName;
    private final List<String> members = new ArrayList<>();

    public void addMember(@NotNull CorePlayer member) {
        members.add(member.getName().toLowerCase());

        PartyManager.INSTANCE.addMemberToParty(this, member);

        for (val memberName : members) {
//            if (!memberName.equalsIgnoreCase(member.getName())) {
//                NekoCore.getInstance().executeBroadcast(ModuleExecuteType.INSERT,
//                        "CoreParty", "PLAYER_PARTY_" + memberName, this);
//            } else {
//                NekoCore.getInstance().executeBroadcast(ModuleExecuteType.DELETE,
//                        "CoreParty", "PLAYER_PARTY_" + memberName, null);
//            }
        }
    }

    public void removeMember(CorePlayer member) {
        for (val memberName : members) {
//            if (!memberName.equalsIgnoreCase(member.getName())) {
//                NekoCore.getInstance().executeBroadcast(ModuleExecuteType.INSERT,
//                        "CoreParty", "PLAYER_PARTY_" + memberName, this);
//                continue;
//            }
//
//            NekoCore.getInstance().executeBroadcast(ModuleExecuteType.DELETE,
//                    "CoreParty", "PLAYER_PARTY_" + memberName, null);
        }
        members.remove(member.getName().toLowerCase());
        PartyManager.INSTANCE.removeMemberToParty(member);
    }

    public boolean isLeader(CorePlayer corePlayer) {
        return corePlayer.isOnline() &&
                leaderName.equalsIgnoreCase(corePlayer.getName());
    }

    public boolean isMember(CorePlayer corePlayer) {
        return members.contains(corePlayer.getName().toLowerCase()) &&
                !isLeader(corePlayer);
    }

    public void alert(@NotNull String alertMessage) {
        for (val corePlayer : members.stream()
                .map(player -> (NekoCore.getInstance()).getPlayer(player)).toList()) {
            if (corePlayer == null || !corePlayer.isOnline()) continue;
            corePlayer.sendMessage(alertMessage);
        }
    }

    public void alertLocale(@NotNull String messageKey) {
        alertLocale(messageKey, (Object) new String[0]);
    }

    public void warp(@NotNull Bukkit bukkitServer) {
        for (val corePlayer : members.stream()
                .map(player -> (NekoCore.getInstance()).getPlayer(player)).toList()) {

            corePlayer.redirect(bukkitServer);
        }
    }

    public void alertLocale(@NotNull String messageKey, Object... placeholders) {
        for (val corePlayer : members.stream()
                .map(player -> (NekoCore.getInstance()).getPlayer(player)).toList()) {

            if (corePlayer == null || !corePlayer.isOnline()) continue;

            corePlayer.sendMessageLocale(messageKey, placeholders);
        }
    }

}