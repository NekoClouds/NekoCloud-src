package me.nekocloud.vanish;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Set;

public abstract class VanishManager {

    protected final Vanish vanish;

    public VanishManager(Vanish vanish) {
        this.vanish = vanish;
    }

    public abstract boolean isVanished(final Player uuid);

    public abstract void setVanishedState(final Player uuid, String name, boolean hide, String causeName);

    public final void setVanishedState(final Player uuid, String name, boolean hide) {
        setVanishedState(uuid, name, hide, null);
    }

    public abstract Set<Player> getVanishedPlayers();

    public abstract Collection<Player> getOnlineVanishedPlayers();
}
