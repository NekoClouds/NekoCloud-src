package me.nekocloud.lobby.npc;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.depend.PacketObject;
import me.nekocloud.api.entity.npc.types.HumanNPC;
import me.nekocloud.api.hologram.Hologram;
import me.nekocloud.api.hologram.HologramAPI;
import me.nekocloud.base.locale.Language;

public abstract class LobbyNPC {

    protected static final HologramAPI HOLOGRAM_API = NekoCloud.getHologramAPI();

    @Getter
    protected final HumanNPC humanNPC;

    protected final TIntObjectMap<Hologram> holograms = new TIntObjectHashMap<>();

    protected LobbyNPC(HumanNPC humanNPC) {
        this.humanNPC = humanNPC;
    }

    public final Hologram getHologram(Language lang) {
        Hologram hologram = holograms.get(lang.getId());
        if (hologram != null)
            return hologram;

        return holograms.get(Language.DEFAULT.getId());
    }

    public final void remove() {
        holograms.valueCollection().forEach(PacketObject::remove);
        humanNPC.remove();
    }
}
