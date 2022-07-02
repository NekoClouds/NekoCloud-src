package me.nekocloud.api.event.gamer;

import lombok.Getter;
import lombok.Setter;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.base.locale.Language;
import org.bukkit.event.Cancellable;

@Getter
public class GamerChangeLanguageEvent extends GamerEvent implements Cancellable {

    private final Language language;
    private final Language oldLanguage;

    @Setter
    private boolean cancelled;

    public GamerChangeLanguageEvent(
            final BukkitGamer gamer,
            final Language language,
            final Language oldLanguage
    ) {
        super(gamer);

        this.language = language;
        this.oldLanguage = oldLanguage;
    }
}
