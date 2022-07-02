package pw.novit.nekocloud.bungee.api.event.gamer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import me.nekocloud.base.gamer.constans.SettingsType;
import pw.novit.nekocloud.bungee.api.event.GamerEvent;
import pw.novit.nekocloud.bungee.api.gamer.BungeeGamer;

@ToString
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class GamerChangeSettingEvent extends GamerEvent {
    SettingsType settingsType;
    boolean result;

    public GamerChangeSettingEvent(
            final BungeeGamer gamer,
            final SettingsType settingsType,
            final boolean result
    ) {
        super(gamer);
        this.settingsType = settingsType;
        this.result = result;
    }
}
