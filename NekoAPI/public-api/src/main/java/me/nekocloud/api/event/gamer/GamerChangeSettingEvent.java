package me.nekocloud.api.event.gamer;

import lombok.Getter;
import me.nekocloud.api.player.BukkitGamer;
import me.nekocloud.base.gamer.constans.SettingsType;

@Getter
public class GamerChangeSettingEvent extends GamerEvent {

    private final SettingsType setting;
    private final boolean result;

    public GamerChangeSettingEvent(BukkitGamer gamer, SettingsType setting, boolean result) {
        super(gamer);
        this.setting = setting;
        this.result = result;
    }
}
