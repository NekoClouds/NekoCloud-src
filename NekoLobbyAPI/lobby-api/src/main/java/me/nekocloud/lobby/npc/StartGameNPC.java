package me.nekocloud.lobby.npc;

import lombok.Getter;
import lombok.val;
import me.nekocloud.api.entity.npc.types.HumanNPC;
import me.nekocloud.api.hologram.Hologram;
import me.nekocloud.base.locale.Language;
import me.nekocloud.lobby.api.game.GameUpdateType;
import me.nekocloud.lobby.game.data.Channel;
import org.bukkit.Location;

public final class StartGameNPC extends LobbyNPC {

    @Getter
    private final Channel channel;

    public StartGameNPC(String name, Location location, Channel channel, HumanNPC humanNPC, GameUpdateType gameUpdateType) {
        super(humanNPC);
        this.channel = channel;

        for (val language : Language.values()) {
            val dependString = (gameUpdateType == GameUpdateType.DEFAULT ? ""
                    : " " + gameUpdateType.getChatColor() + language.getMessage(gameUpdateType.getKey()));

            Hologram hologram = HOLOGRAM_API.createHologram(location.clone().add(0.0, 1.9, 0.0));
            hologram.addTextLine("§b§l" + name + dependString);
            hologram.addAnimationLine(20 * 3, new OnlineReplacer(channel, language));
            hologram.addTextLine(language.getMessage("HOLO_SELECTOR_GAME_CHANNEL"));
            holograms.put(language.getId(), hologram);
        }
    }

}
