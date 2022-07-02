package me.nekocloud.party.core.listener;

import me.nekocloud.core.api.event.EventHandler;
import me.nekocloud.core.api.event.EventListener;
import me.nekocloud.core.api.event.player.PlayerQuitEvent;
import me.nekocloud.party.core.type.Party;
import me.nekocloud.party.core.type.PartyManager;

public final class PartyHavingListener implements EventListener {

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Party party = PartyManager.INSTANCE.getParty(event.getCorePlayer());

        if (party != null) {
            if (party.isLeader(event.getCorePlayer())) {
                party.alert("§d§lКОМПАНИЯ §8| " + event.getCorePlayer().getDisplayName() + " §fраспустил группу!");
                PartyManager.INSTANCE.deleteParty(party);
            } else {
                party.alert("§6§lКОМПАНИЯ §8| " + event.getCorePlayer().getDisplayName() + " §fпокинул сервер и Вашу группу заодно!");
                party.removeMember(event.getCorePlayer());
                if (party.getMembers().size() <= 1) {
                    party.alert("§6§lКОМПАНИЯ §8| §fГруппа была расформирована так как все ее участники вышли!");
                    PartyManager.INSTANCE.deleteParty(party);
                }
            }
        }
    }
}

