package me.nekocloud.lobby.placeholder.depend;

import lombok.Getter;
import lombok.experimental.FieldDefaults;
import me.nekocloud.base.game.GameType;
import me.nekocloud.base.locale.Language;
import me.nekocloud.lobby.placeholder.hologram.AbstractHolderHolo;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import static lombok.AccessLevel.PRIVATE;

@Getter
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class GameInfoHolo extends AbstractHolderHolo {

    Language lang;

    public GameInfoHolo(final @NotNull Language lang, final @NotNull Location loc) {
        super(loc.clone());
        this.lang = lang;

        hologram.addTextLine(lang.getList("HOLO_GAME_INFO" + "_" + GameType.current.name()));
    }
}
