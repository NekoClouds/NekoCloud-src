package me.nekocloud.party.core.type;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.val;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.api.connection.player.CorePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PartyManager {

    public static final PartyManager INSTANCE = new PartyManager();

    private final Map<String, Party> partyMap = new HashMap<>();


    public void addMemberToParty(@NotNull Party coreParty, @NotNull CorePlayer player) {
        partyMap.put(player.getName().toLowerCase(), coreParty);
    }

    public void removeMemberToParty(@NotNull CorePlayer player) {
        partyMap.remove(player.getName().toLowerCase()).removeMember(player);
    }

    public Party getParty(@NotNull CorePlayer player) {
        return partyMap.get(player.getName().toLowerCase());
    }

    public Party createParty(@NotNull CorePlayer player) {
        Party party = new Party(player.getName());
        party.addMember(player);

        partyMap.put(player.getName().toLowerCase(), party);
        return party;
    }

    public void deleteParty(@NotNull Party party) {
        partyMap.remove(party.getLeaderName().toLowerCase());

        for (val memberPlayer : party.getMembers().stream()
                .map(player -> (NekoCore.getInstance()).getOfflinePlayer(player))
                .collect(Collectors.toSet())) {

            party.removeMember(memberPlayer);
        }
    }

    public boolean hasParty(@NotNull CorePlayer player) {
        return (getParty(player) != null);
    }

}
