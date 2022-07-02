package me.nekocloud.packetlib.nms.scoreboard;

import lombok.Getter;
import lombok.Setter;
import me.nekocloud.api.scoreboard.Collides;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class DTeam implements Cloneable {
    private final String displayName;
    private final String name;

    private boolean disableFire = true;
    private boolean setFriendInv = false;
    private String prefix = "";
    private String suffix = "";
    private TagVisibility visibility = TagVisibility.ALWAYS;
    private Collides collides = Collides.NEVER;

    private final List<String> players = Collections.synchronizedList(new ArrayList<>());

    public DTeam(String name) {
        if (name.length() > 16) {
            this.name = name.substring(0, 15);
        } else {
            this.name = name;
        }

        this.displayName = name;
    }

    public DTeam(String name, String prefix, String suffix) {
        this(name);
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public DTeam(String name, Collection<String> players) {
        this(name);
        this.players.addAll(players);
    }

    public void addPlayer(String name) {
        this.players.add(name);
    }

    public boolean removePlayer(String name) {
        return this.players.remove(name);
    }

    public void addPlayers(Collection<String> names) {
        this.players.addAll(names);
    }

    public int packOptionData() {
        int data = 0;
        if (disableFire) {
            data |= 1;
        }

        if (setFriendInv) {
            data |= 2;
        }

        return data;
    }

    @Override
    public DTeam clone() {
        try {
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return (DTeam) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
