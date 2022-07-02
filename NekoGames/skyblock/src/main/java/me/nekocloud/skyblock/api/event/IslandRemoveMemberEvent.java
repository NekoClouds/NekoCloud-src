package me.nekocloud.skyblock.api.event;

import me.nekocloud.skyblock.api.event.absract.IslandMemberEvent;
import me.nekocloud.skyblock.api.island.Island;

public class IslandRemoveMemberEvent extends IslandMemberEvent {

    public IslandRemoveMemberEvent(Island island, int memberID) {
        super(island, memberID);
    }
}
