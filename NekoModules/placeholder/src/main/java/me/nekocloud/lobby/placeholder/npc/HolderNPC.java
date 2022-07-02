package me.nekocloud.lobby.placeholder.npc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import me.nekocloud.api.NekoCloud;
import me.nekocloud.api.depend.PacketObject;
import me.nekocloud.api.entity.npc.types.HumanNPC;
import me.nekocloud.api.hologram.Hologram;
import me.nekocloud.api.hologram.HologramAPI;
import me.nekocloud.base.game.GameType;
import me.nekocloud.base.locale.Language;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static lombok.AccessLevel.PRIVATE;

@Getter
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class HolderNPC {

    static HologramAPI hologramAPI = NekoCloud.getHologramAPI();

    HumanNPC npc;
    String action;
    Map<Integer, Hologram> holograms = new HashMap<>();

    public HolderNPC(@NotNull HumanNPC npc,
                     String action,
                     String holoKey,
                     @NotNull Location location,
                     GameType gameType
    ) {
        this.npc = npc;
        this.action = action;
    }

    public Hologram getHologram(@NotNull Language lang) {
        return holograms.computeIfAbsent(lang.getId(), integer ->
                holograms.get(Language.RUSSIAN.getId()));
    }

    public void remove() {
        holograms.values().forEach(PacketObject::remove);
        npc.remove();
    }

    @AllArgsConstructor
    @FieldDefaults(level = PRIVATE, makeFinal = true)
    private static class NPCOnlineUpdater implements Supplier<String> {
        Language lang;
        GameType gameType;

        @Override
        public String get() {

            return null;

        }
    }
}
