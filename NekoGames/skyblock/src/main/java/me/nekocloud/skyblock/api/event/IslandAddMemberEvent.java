package me.nekocloud.skyblock.api.event;

import me.nekocloud.skyblock.api.event.absract.IslandMemberEvent;
import me.nekocloud.skyblock.api.island.Island;

public class IslandAddMemberEvent extends IslandMemberEvent {

    public IslandAddMemberEvent(Island island, int memberID) {
        super(island, memberID);
    }
}
