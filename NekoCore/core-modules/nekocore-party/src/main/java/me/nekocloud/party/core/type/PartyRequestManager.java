package me.nekocloud.party.core.type;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import lombok.Getter;
import me.nekocloud.core.NekoCore;
import me.nekocloud.core.common.NetworkManager;
import me.nekocloud.core.api.connection.player.CorePlayer;

import java.util.Collection;
import java.util.stream.Collectors;

public final class PartyRequestManager {

    public static final PartyRequestManager INSTANCE = new PartyRequestManager();

    @Getter
    private final Multimap<Integer, Integer> partyRequestsIds = MultimapBuilder.SetMultimapBuilder
            .linkedHashKeys()
            .linkedHashSetValues().build();


    public void addPartyRequest(int playerID, int targetID) {
        partyRequestsIds.put(targetID, playerID);
    }

    public void removePartyRequest(int playerID, int targetID) {
        partyRequestsIds.remove(targetID, playerID);
    }

    public boolean hasPartyRequest(int playerID, int targetID) {
        return partyRequestsIds.containsEntry(targetID, playerID);
    }


    public Collection<Integer> getPartiesRequestsIds(int playerID) {
        return partyRequestsIds.get(playerID);
    }

    public Collection<CorePlayer> getOfflinePartiesIds(int playerID) {
        return getPartiesRequestsIds(playerID).stream()
                .map(targetID -> NekoCore.getInstance().getOfflinePlayer(NetworkManager.INSTANCE.getPlayerName(targetID)))
                .collect(Collectors.toList());
    }

    public Collection<Integer> removeAll(int playerID) {
        return partyRequestsIds.removeAll(playerID);
    }
}
