package pw.novit.nekocloud.bungee.api.event.gamer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import me.nekocloud.base.locale.Language;
import pw.novit.nekocloud.bungee.api.event.GamerEvent;
import pw.novit.nekocloud.bungee.api.gamer.BungeeGamer;

@Getter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class GamerChangeLanguageEvent extends GamerEvent {
    Language newLanguage;
    Language oldLanguage;

    public GamerChangeLanguageEvent(
            final BungeeGamer gamer,
            final Language newLanguage,
            final Language oldLanguage
    ) {
        super(gamer);
        this.newLanguage = newLanguage;
        this.oldLanguage = oldLanguage;
    }
}
