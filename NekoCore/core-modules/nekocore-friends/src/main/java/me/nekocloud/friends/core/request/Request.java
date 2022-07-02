package me.nekocloud.friends.core.request;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import me.nekocloud.core.api.connection.player.CorePlayer;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Getter
public class Request {

    IntSet incoming = new IntOpenHashSet();
    IntSet outgoing = new IntOpenHashSet();

    CorePlayer corePlayer;

    public boolean sentInvite(Integer to) {
        return outgoing.contains(to);
    }

    public boolean hasInvite(Integer from) {
        return incoming.contains(from);
    }

    public int getId() {
        return corePlayer.getPlayerID();
    }

    public void sendInvite(Integer who) {
        outgoing.add(who);
    }

    public void recieveInvite(Integer from) {
        incoming.add(from);
    }

    public void deleteInvite(Integer from) {
        incoming.remove(from);
    }

    public void cancelInvite(Integer to) {
        outgoing.remove(to);
    }
}
